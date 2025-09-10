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
package com.aston.cloudthread.core.monitor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.aston.cloudthread.core.config.ApplicationProperties;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorWrapper;
import com.aston.cloudthread.core.toolkit.ThreadFactoryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
public class ThreadPoolMonitor {
    private ScheduledExecutorService scheduler;
    private Map<String, ThreadPoolRuntimeContext> micrometerMonitorCache;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private static final String METRIC_NAME_PREFIX = "dynamic.thread-pool";
    private static final String DYNAMIC_THREAD_POOL_ID_TAG = METRIC_NAME_PREFIX + ".id";
    private static final String APPLICATION_NAME_TAG = "application.name";

    /**
     * Setup scheduled checking tasks.
     */
    public void start() {
        BootstrapConfigProperties.MonitorConfig monitorConfig =
                BootstrapConfigProperties.getInstance().getMonitorConfig();
        if (!monitorConfig.getEnable()) {
            return;
        }

        // init monitor config
        micrometerMonitorCache = new ConcurrentHashMap<>();
        scheduler = Executors.newScheduledThreadPool(
                1,
                ThreadFactoryBuilder.builder()
                        .namePrefix("scheduler_thread-pool_monitor")
                        .build()
        );

        // setup delay 0 seconds
        scheduler.scheduleWithFixedDelay(() -> {
            Collection<ThreadPoolExecutorWrapper> wrappers =
                    CloudThreadRegistry.getAllWrappers();
            for (ThreadPoolExecutorWrapper wrapper : wrappers) {
                ThreadPoolRuntimeContext runtimeContext =
                        buildThreadPoolRuntimeContext(wrapper);

                // check metric collect type
                if (Objects.equals(monitorConfig.getCollectType(), "log")) {
                    logMonitor(runtimeContext);
                } else if (Objects.equals(monitorConfig.getCollectType(), "micrometer")) {
                    micrometerMonitor(runtimeContext);
                }
            }
        }, 0, monitorConfig.getCollectIntervalSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Disable monitor scheduled validation
     */
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public void logMonitor(ThreadPoolRuntimeContext runtimeContext) {
        if (runtimeContext == null) {
            log.warn("[ThreadPool Monitor] runtimeContext is null");
            return;
        }
        try {
            String runtimeInfoJson = OBJECT_MAPPER.writeValueAsString(runtimeContext);
            log.info("[ThreadPool Monitor] {} | Content: {}", runtimeContext.getThreadPoolUID(), runtimeInfoJson);
        } catch (JsonProcessingException e) {
            log.error("[ThreadPool Monitor] {} | Failed to serialize runtime info",
                    runtimeContext.getThreadPoolUID(), e);
        }
    }

    public void micrometerMonitor(ThreadPoolRuntimeContext ctx) {
        String threadPoolUID = ctx.getThreadPoolUID();
        ThreadPoolRuntimeContext existingRuntimeInfo = micrometerMonitorCache.get(threadPoolUID);
        if (existingRuntimeInfo != null) {
            BeanUtil.copyProperties(ctx, existingRuntimeInfo);
        } else {
            micrometerMonitorCache.put(threadPoolUID, ctx);
        }

        Iterable<Tag> tags = CollectionUtil.newArrayList(
                Tag.of(DYNAMIC_THREAD_POOL_ID_TAG, threadPoolUID),
                Tag.of(APPLICATION_NAME_TAG, ApplicationProperties.getApplicationName()));

        Metrics.gauge(metricName("core.size"), tags, ctx, ThreadPoolRuntimeContext::getCorePoolSize);
        Metrics.gauge(metricName("maximum.size"), tags, ctx, ThreadPoolRuntimeContext::getMaximumPoolSize);
        Metrics.gauge(metricName("current.size"), tags, ctx, ThreadPoolRuntimeContext::getActivePoolSize);
        Metrics.gauge(metricName("largest.size"), tags, ctx, ThreadPoolRuntimeContext::getLargestPoolSize);
        Metrics.gauge(metricName("active.size"), tags, ctx, ThreadPoolRuntimeContext::getActivePoolSize);
        Metrics.gauge(metricName("queue.size"), tags, ctx, ThreadPoolRuntimeContext::getWorkQueueSize);
        Metrics.gauge(metricName("queue.capacity"), tags, ctx, ThreadPoolRuntimeContext::getWorkQueueCapacity);
        Metrics.gauge(metricName("queue.remaining.capacity"), tags, ctx, ThreadPoolRuntimeContext::getWorkQueueRemainingCapacity);
        Metrics.gauge(metricName("completed.task.count"), tags, ctx, ThreadPoolRuntimeContext::getCompletedTaskCount);
        Metrics.gauge(metricName("reject.count"), tags, ctx, ThreadPoolRuntimeContext::getRejectCount);
    }

    private String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    // -- build context of ThreadPoolRuntime ---
    @SneakyThrows
    public ThreadPoolRuntimeContext buildThreadPoolRuntimeContext(ThreadPoolExecutorWrapper wrapper) {
        ThreadPoolExecutor executor = wrapper.getExecutor();
        BlockingQueue<?> queue = executor.getQueue();

        long rejectCount = -1L;
        if (executor instanceof CloudThreadExecutor) {
            rejectCount = ((CloudThreadExecutor) executor).getRejectCount().get();
        }

        int workQueueSize = queue.size(); // API supports lock, avoid high frequency invoke
        int remainingCapacity = queue.remainingCapacity(); // API supports lock, avoid high frequency invoke
        return ThreadPoolRuntimeContext.builder()
                .threadPoolUID(wrapper.getThreadPoolUID())
                .corePoolSize(executor.getCorePoolSize())
                .maximumPoolSize(executor.getMaximumPoolSize())
                .activePoolSize(executor.getActiveCount())  // API supports lock, avoid high frequency invoke
                .currentPoolSize(executor.getPoolSize())  // API supports lock, avoid high frequency invoke
                .completedTaskCount(executor.getCompletedTaskCount())  // API supports lock, avoid high frequency invoke
                .largestPoolSize(executor.getLargestPoolSize())  // API supports lock, avoid high frequency invoke
                .workQueueName(queue.getClass().getSimpleName())
                .workQueueSize(workQueueSize)
                .workQueueRemainingCapacity(remainingCapacity)
                .workQueueCapacity(workQueueSize + remainingCapacity)
                .rejectedHandlerName(executor.getRejectedExecutionHandler().toString())
                .rejectCount(rejectCount)
                .build();
    }
}
