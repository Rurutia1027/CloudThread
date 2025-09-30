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
package com.aston.cloudthread.spring.base.support;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.spring.base.CloudDynamicThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class CloudThreadBeanPostProcessor implements BeanPostProcessor {
    private final BootstrapConfigProperties properties;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CloudThreadExecutor) {
            CloudDynamicThreadPool cloudDynamicThreadPool;
            try {
                // Check whether the bean is annotated with @CloudDynamicThreadPool
                cloudDynamicThreadPool =
                        ApplicationContextHolder.findAnnotationOnBean(beanName,
                                CloudDynamicThreadPool.class);
                if (Objects.isNull(cloudDynamicThreadPool)) {
                    return bean;
                }
            } catch (Exception ex) {
                log.error("Failed to create cloud dynamic thread pool in annotation mode.",
                        ex);
                return bean;
            }

            CloudThreadExecutor cloudThreadExecutor = (CloudThreadExecutor) bean;

            // Load thread pool configuration from the config center
            // and apply it to the CloudThreadExecutor
            ThreadPoolExecutorProperties executorProperties = properties.getExecutors()
                    .stream()
                    .filter(item -> Objects.equals(cloudThreadExecutor.getThreadPoolUID(),
                            item.getThreadPoolUID()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("The Thread Pool UID does " +
                            "not exist in the configuration."));
            overrideLocalThreadPoolConfig(executorProperties, cloudThreadExecutor);

            // Register the CloudThreadExecutor into the CloudThreadRegistry.
            // ThreadPoolMonitor and alarms will later retrieve the thread pool instances
            // directly from the registry by its threadPoolUID
            CloudThreadRegistry.putWrapper(cloudThreadExecutor.getThreadPoolUID(),
                    cloudThreadExecutor, executorProperties);
        }
        return bean;
    }

    /**
     * Override the configuration of a local {@link CloudThreadExecutor} with values provided
     * from remote {@link ThreadPoolExecutorProperties}.
     *
     * <p>
     * The method:
     *     <ul>
     *         <li>Validates the corePoolSize does not exceed maximumPoolSize</li>
     *         <li>Adjusts pool sizes based on remote configuration.</li>
     *         <li>Replaces the work queue via reflection (since ThreadPoolExecutor has
     *         not setter)</li>
     *         <li>Updates keep-alive time, core-thread timeout setting, and rejection
     *         policy.</li>
     *     </ul>
     * </p>
     *
     * @param executorProperties  Remote thread pool configuration
     * @param cloudThreadExecutor Local thread pool instance to update
     */
    private void overrideLocalThreadPoolConfig(ThreadPoolExecutorProperties executorProperties,
                                               CloudThreadExecutor cloudThreadExecutor) {
        Integer remoteCorePoolSize = executorProperties.getCorePoolSize();
        Integer remoteMaximumPoolSize = executorProperties.getMaximumPoolSize();
        Assert.isTrue(remoteCorePoolSize <= remoteMaximumPoolSize, "[remoteCorePoolSize] must " +
                "be smaller than [remoteMaximumPoolSize]");

        int originalMaximumPoolSize = cloudThreadExecutor.getMaximumPoolSize();
        if (remoteCorePoolSize > originalMaximumPoolSize) {
            cloudThreadExecutor.setMaximumPoolSize(remoteMaximumPoolSize);
            cloudThreadExecutor.setCorePoolSize(remoteCorePoolSize);
        } else {
            cloudThreadExecutor.setCorePoolSize(remoteCorePoolSize);
            cloudThreadExecutor.setMaximumPoolSize(remoteMaximumPoolSize);
        }

        // 1. Set the BlockingQueue. If the configuration is null, keep the original queue.
        BlockingQueue workQueue = executorProperties.getWorkingQueue() != null
                ? BlockingQueueTypeEnum.createBlockingQueue(executorProperties.getWorkingQueue(), executorProperties.getQueueCapacity())
                : cloudThreadExecutor.getQueue(); // keep the original

        ReflectUtil.setFieldValue(cloudThreadExecutor, "workQueue", workQueue);

        // 2. Set the keep-alive time. If null, keep the original value or use the default (e.g., 60 seconds)
        long keepAlive = executorProperties.getKeeAliveTimeSeconds() != null
                ? executorProperties.getKeeAliveTimeSeconds()
                : cloudThreadExecutor.getKeepAliveTime(TimeUnit.SECONDS); // or 60L
        cloudThreadExecutor.setKeepAliveTime(keepAlive, TimeUnit.SECONDS);

        // 3. Set allowCoreThreadTimeout. If null, keep the original value or use default false
        boolean allowCoreTimeout = executorProperties.getAllowCoreThreadTimeout() != null
                ? executorProperties.getAllowCoreThreadTimeout()
                : false; // default false
        cloudThreadExecutor.allowCoreThreadTimeOut(allowCoreTimeout);

        // 4. Set the RejectedExecutionHandler. If null, use the default AbortPolicy
        cloudThreadExecutor.setRejectedExecutionHandler(
                executorProperties.getRejectedHandler() != null
                        ? new ThreadPoolExecutor.AbortPolicy()
                        : new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
