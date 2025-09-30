/*
 * Copyright 2024 Rurutia1027
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.aston.cloudthread.config.common.starter.refresher;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorWrapper;
import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.core.executor.support.RejectedPolicyTypeEnum;
import com.aston.cloudthread.core.executor.support.ResizableCapacityLinkedBlockingQueue;
import com.aston.cloudthread.core.notification.dto.ThreadPoolConfigChangeDTO;
import com.aston.cloudthread.core.notification.service.NotifierDispatcher;
import com.aston.cloudthread.spring.base.support.ApplicationContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.aston.cloudthread.core.constant.Constants.CHANGE_DELIMITER;
import static com.aston.cloudthread.core.constant.Constants.CHANGE_THREAD_POOL_TEXT;

/**
 * CloudThreadPoolRefresh Listener
 *
 * <p>
 * ApplicationListener that listens for {@link CloudThreadPoolConfigUpdateEvent}.
 * When a remote configuration update is published, this listener:
 * <ul>
 *     <li>Checks if the event carries valid executor configuration.</li>
 *     <li>Compares remote and local thread pool properties.</li>
 *     <li>If differences exist, synchronizes local thread pool settings with the remote
 *     config.</li>
 *     <li>Updates the {@link CloudThreadRegistry} with new properties.</li>
 *     <li>Sends notifications about the change through {@link NotifierDispatcher}.</li>
 *     <li>Logs detailed change information (core size, max size, queue capacity, etc.). </li>
 * </ul>
 * </p>
 * <p>
 *     Synchronization is done per-thread-pool UID (via {@link String#intern()}) to avoid
 *     race conditions during concurrent config updates.
 * </p>
 *
 * <p>
 *     This mechanism ensures that CloudThread maintains consistent thread pool configurations
 *     across distributed environments where config way change dynamically (e.g., from
 *     remote config center).
 * </p>
 */

@Slf4j
@RequiredArgsConstructor
public class CloudThreadPoolRefreshListener implements ApplicationListener<CloudThreadPoolConfigUpdateEvent> {

    private final NotifierDispatcher notifierDispatcher;

    @Override
    public void onApplicationEvent(CloudThreadPoolConfigUpdateEvent event) {
        BootstrapConfigProperties refresherProperties = event.getBootstrapConfigProperties();

        // Skip if no executor configuration is provided in the remote config.
        if (CollUtil.isEmpty(refresherProperties.getExecutors())) {
            return;
        }

        // Iterate over each remote thread pool configuration and sync with local
        for (ThreadPoolExecutorProperties remoteProps : refresherProperties.getExecutors()) {
            String threadPoolUID = remoteProps.getThreadPoolUID();

            // Synchronize updates per thread pool UID to prevent concurrent modification
            // issues.
            synchronized (threadPoolUID.intern()) {
                // Check if the remote config differs from the current local config.
                if (!hasThreadPoolConfigChanged(remoteProps)) {
                    log.info("no change, skip synchronized");
                    continue;
                }

                // Apply remote configuration to the local thread pool instance.
                updateThreadPoolFromRemoteConfig(remoteProps);

                // Retrieve wrapper and updates its cached properties.
                ThreadPoolExecutorWrapper wrapper =
                        CloudThreadRegistry.getWrapper(threadPoolUID);
                ThreadPoolExecutorProperties originalProps = wrapper.getExecutorProperties();
                wrapper.setExecutorProperties(remoteProps);

                // Dispatch notification about the configuration update.
                sendThreadPoolConfigChangeMessage(originalProps, remoteProps);

                // Log detailed changes between old and new properties.
                log.info(CHANGE_THREAD_POOL_TEXT,
                        threadPoolUID,
                        String.format(CHANGE_DELIMITER, originalProps.getCorePoolSize(),
                                remoteProps.getCorePoolSize()),
                        String.format(CHANGE_DELIMITER, originalProps.getMaximumPoolSize(), remoteProps.getMaximumPoolSize()),
                        String.format(CHANGE_DELIMITER, originalProps.getQueueCapacity(), remoteProps.getQueueCapacity()),
                        String.format(CHANGE_DELIMITER, originalProps.getKeepAliveTimeSeconds(),
                                remoteProps.getKeepAliveTimeSeconds()),
                        String.format(CHANGE_DELIMITER, originalProps.getRejectedHandler(), remoteProps.getRejectedHandler()),
                        String.format(CHANGE_DELIMITER, originalProps.getAllowCoreThreadTimeout(),
                                remoteProps.getAllowCoreThreadTimeout())
                );
            }
        }
    }

    /**
     * Sends a thread pool configuration change notification to subscribed channels.
     *
     * <p>
     * This method constructs a {@link ThreadPoolConfigChangeDTO} containing the changes
     * between the original and remote thread pool properties, including core pool size,
     * maximum pool size, queue capacity, rejected handler, and keep-alive time.
     * </p>
     * <p>
     * The {@link NotifierDispatcher} is responsible for delivering the notification to
     * multiple platforms such as Teams, Slack, Discord, or other subscriber endpoints.
     * </p>
     * <ul>
     *     <li>Active Spring profile</li>
     *     <li>Application name</li>
     *     <li>Thread pool UID</li>
     *     <li>Subscribers list</li>
     *     <li>Local host IP address</li>
     *     <li>Update timestamp</li>
     * </ul>
     *
     * @param originalProps The current local thread pool properties before the update.
     * @param remoteProps   The updated thread pool properties from remote configurations.
     */
    @SneakyThrows
    private void sendThreadPoolConfigChangeMessage(ThreadPoolExecutorProperties originalProps,
                                                   ThreadPoolExecutorProperties remoteProps) {

        // Fetch spring app env properties instance directly from spring context via
        // ApplicationContextHolder
        Environment environment = ApplicationContextHolder.getBean(Environment.class);
        String activeProfile = environment.getProperty("spring.profiles.active", "dev");
        String applicationName = environment.getProperty("spring.application.name");

        Map<String, ThreadPoolConfigChangeDTO.ChangePair<?>> changes = new HashMap<>();
        changes.put("corePoolSize", new ThreadPoolConfigChangeDTO.ChangePair<>(originalProps.getCorePoolSize(), remoteProps.getCorePoolSize()));
        changes.put("maximumPoolSize", new ThreadPoolConfigChangeDTO.ChangePair<>(originalProps.getMaximumPoolSize(), remoteProps.getMaximumPoolSize()));
        changes.put("queueCapacity", new ThreadPoolConfigChangeDTO.ChangePair<>(originalProps.getQueueCapacity(), remoteProps.getQueueCapacity()));
        changes.put("rejectedHandler", new ThreadPoolConfigChangeDTO.ChangePair<>(originalProps.getRejectedHandler(), remoteProps.getRejectedHandler()));
        changes.put("keepAliveTime",
                new ThreadPoolConfigChangeDTO.ChangePair<>(
                        originalProps.getKeepAliveTimeSeconds(),
                        remoteProps.getKeepAliveTimeSeconds()));

        ThreadPoolConfigChangeDTO configChangeDTO = ThreadPoolConfigChangeDTO.builder()
                .activeProfile(activeProfile)
                .identify(InetAddress.getLocalHost().getHostAddress())
                .applicationName(applicationName)
                .threadPoolUID(originalProps.getThreadPoolUID())
                .subscribers(originalProps.getNotify().getSubscribers())
                .workQueue(originalProps.getWorkingQueue())
                .changes(changes)
                .updateTime(DateUtil.now())
                .build();
        notifierDispatcher.sendChangeMessage(configChangeDTO);
    }

    /**
     * Synchronizes the local thread pool configuration with the given remote properties.
     *
     * <p>Updates core/max pool sizes, keep-alive time, allowCoreThreadTimeout,
     * rejected handler, and queue capacity (if supported). Ensures proper update order
     * to avoid exceptions and ignores null values.
     *
     * @param remoteProps Remote thread pool properties to apply.
     */
    private void updateThreadPoolFromRemoteConfig(ThreadPoolExecutorProperties remoteProps) {
        String threadPoolUID = remoteProps.getThreadPoolUID();
        ThreadPoolExecutorWrapper wrapper = CloudThreadRegistry.getWrapper(threadPoolUID);
        ThreadPoolExecutor executor = wrapper.getExecutor();
        ThreadPoolExecutorProperties originalProps = wrapper.getExecutorProperties();

        Integer remoteCorePoolSize = remoteProps.getCorePoolSize();
        Integer remoteMaximumPoolSize = remoteProps.getMaximumPoolSize();

        if (remoteCorePoolSize != null && remoteMaximumPoolSize != null) {
            int originalMaximumPoolSize = executor.getMaximumPoolSize();
            if (remoteCorePoolSize > originalMaximumPoolSize) {
                executor.setMaximumPoolSize(remoteMaximumPoolSize);
                executor.setCorePoolSize(remoteCorePoolSize);
            } else {
                executor.setCorePoolSize(remoteCorePoolSize);
                executor.setMaximumPoolSize(remoteMaximumPoolSize);
            }
        } else {
            if (remoteMaximumPoolSize != null) {
                executor.setMaximumPoolSize(remoteMaximumPoolSize);
            }
            if (remoteCorePoolSize != null) {
                executor.setCorePoolSize(remoteCorePoolSize);
            }
        }

        if (remoteProps.getAllowCoreThreadTimeout() != null &&
                !Objects.equals(remoteProps.getAllowCoreThreadTimeout(),
                        originalProps.getAllowCoreThreadTimeout())) {
            executor.allowCoreThreadTimeOut(remoteProps.getAllowCoreThreadTimeout());
        }

        if (remoteProps.getRejectedHandler() != null &&
                !Objects.equals(remoteProps.getRejectedHandler(), originalProps.getRejectedHandler())) {
            RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy(remoteProps.getRejectedHandler());
            executor.setRejectedExecutionHandler(handler);
        }

        if (remoteProps.getAllowCoreThreadTimeout() != null &&
                !Objects.equals(remoteProps.getKeepAliveTimeSeconds(), originalProps.getKeepAliveTimeSeconds())) {
            executor.setKeepAliveTime(remoteProps.getKeepAliveTimeSeconds(), TimeUnit.SECONDS);
        }

        /**
         * Adjusts the executor's queue capacity if it differs from the remote configuration.
         * Note: Only effective when the executor uses ResizableCapacityLinkedBlockingQueue.
         */
        if (isQueueCapacityChanged(originalProps, remoteProps, executor)) {
            BlockingQueue<Runnable> queue = executor.getQueue();
            ResizableCapacityLinkedBlockingQueue<?> resizableQueue = (ResizableCapacityLinkedBlockingQueue<?>) queue;
            resizableQueue.setCapacity(remoteProps.getQueueCapacity());
        }
    }

    private boolean hasThreadPoolConfigChanged(ThreadPoolExecutorProperties remoteProps) {
        String threadPoolUID = remoteProps.getThreadPoolUID();
        ThreadPoolExecutorWrapper wrapper = CloudThreadRegistry.getWrapper(threadPoolUID);
        if (wrapper == null) {
            log.warn("No thread pool found for thread pool id: {}", threadPoolUID);
            return false;
        }
        ThreadPoolExecutor executor = wrapper.getExecutor();
        ThreadPoolExecutorProperties originalProps = wrapper.getExecutorProperties();

        return hasDifference(originalProps, remoteProps, executor);
    }


    private boolean hasDifference(ThreadPoolExecutorProperties originalProps,
                                  ThreadPoolExecutorProperties remoteProps,
                                  ThreadPoolExecutor executor) {
        return isChanged(originalProps.getCorePoolSize(), remoteProps.getCorePoolSize())

                || isChanged(originalProps.getMaximumPoolSize(), remoteProps.getMaximumPoolSize())

                || isChanged(originalProps.getAllowCoreThreadTimeout(), remoteProps.getAllowCoreThreadTimeout())

                || isChanged(originalProps.getKeepAliveTimeSeconds(), remoteProps.getKeepAliveTimeSeconds())

                || isChanged(originalProps.getRejectedHandler(), remoteProps.getRejectedHandler())

                || isQueueCapacityChanged(originalProps, remoteProps, executor);
    }

    private boolean isQueueCapacityChanged(ThreadPoolExecutorProperties originalProps,
                                           ThreadPoolExecutorProperties remoteProps,
                                           ThreadPoolExecutor executor) {
        Integer remoteCapacity = remoteProps.getQueueCapacity();
        Integer originalCapacity = originalProps.getQueueCapacity();
        BlockingQueue<?> queue = executor.getQueue();

        return remoteCapacity != null
                && !Objects.equals(remoteCapacity, originalCapacity)
                && Objects.equals(BlockingQueueTypeEnum.RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE.getName(),
                queue.getClass().getSimpleName());
    }


    private <T> boolean isChanged(T before, T after) {
        return after != null && !Objects.equals(before, after);
    }
}
