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
package com.aston.cloudthread.web.starter.core.service;

import cn.hutool.core.util.ReflectUtil;
import com.aston.cloudthread.core.constant.Constants;
import com.aston.cloudthread.web.starter.core.config.WebThreadPoolConfig;
import com.aston.cloudthread.web.starter.core.enums.WebContainerEnum;
import com.aston.cloudthread.web.starter.core.metric.WebThreadPoolMetrics;
import com.aston.cloudthread.web.starter.core.snapshot.WebThreadPoolSnapshot;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.web.embedded.jetty.JettyWebServer;
import org.springframework.boot.web.server.WebServer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

@Slf4j
public class JettyWebThreadPoolService extends AbstractWebThreadPoolService {
    @Override
    protected Executor getWebServerInnerExecutor(WebServer webServer) {
        return ((JettyWebServer) webServer).getServer().getThreadPool();
    }

    @Override
    public void updateThreadPool(WebThreadPoolConfig config) {
        try {
            QueuedThreadPool jettyExecutor = (QueuedThreadPool) webExecutorRef;
            int originalCorePoolSize = jettyExecutor.getMinThreads();
            int originalMaximumPoolSize = jettyExecutor.getMaxThreads();
            long originalKeepAliveTime = jettyExecutor.getIdleTimeout();

            if (config.getCorePoolSize() > originalMaximumPoolSize) {
                jettyExecutor.setMaxThreads(config.getMaximumPoolSize());
                jettyExecutor.setMinThreads(config.getCorePoolSize());
            } else {
                jettyExecutor.setMinThreads(config.getCorePoolSize());
                jettyExecutor.setMaxThreads(config.getMaximumPoolSize());
            }
            jettyExecutor.setIdleTimeout(config.getKeepAliveTimeSeconds().intValue());

            log.info("[Jetty] Changed web thread pool. corePoolSize: {}, maximumPoolSize: {}, keepAliveTime: {}",
                    String.format(Constants.CHANGE_DELIMITER, originalCorePoolSize, config.getCorePoolSize()),
                    String.format(Constants.CHANGE_DELIMITER, originalMaximumPoolSize, config.getMaximumPoolSize()),
                    String.format(Constants.CHANGE_DELIMITER, originalKeepAliveTime, config.getKeepAliveTimeSeconds()));
        } catch (Exception ex) {
            log.error("Failed to modify the Jetty thread pool parameter.", ex);
        }
    }

    @SneakyThrows
    @Override
    public WebThreadPoolMetrics getMetrics() {
        JettyThreadPoolInfo info = extractJettyThreadPoolInfo((QueuedThreadPool) webExecutorRef);

        return WebThreadPoolMetrics.builder()
                .corePoolSize(info.corePoolSize)
                .maximumPoolSize(info.maximumPoolSize)
                .keepAliveTimeSeconds(info.keepAliveTimeSeconds)
                .workQueueName(info.queueName)
                .workQueueSize(info.queueSize)
                .workQueueRemainingCapacity(info.remainingCapacity)
                .workQueueCapacity(info.queueCapacity)
                .rejectedHandlerName(info.rejectedHandlerName)
                .build();
    }

    @Override
    public WebThreadPoolSnapshot getRuntimeSnapshot() {
        JettyThreadPoolInfo info = extractJettyThreadPoolInfo((QueuedThreadPool) webExecutorRef);

        return WebThreadPoolSnapshot.builder()
                .corePoolSize(info.corePoolSize)
                .maximumPoolSize(info.maximumPoolSize)
                .activePoolSize(info.activeCount)
                .currentPoolSize(info.currentPoolSize)
                .keepAliveTimeSeconds(info.keepAliveTimeSeconds)
                .workQueueName(info.queueName)
                .workQueueSize(info.queueSize)
                .workQueueRemainingCapacity(info.remainingCapacity)
                .workQueueCapacity(info.queueCapacity)
                .rejectedHandlerName(info.rejectedHandlerName)
                .build();
    }

    /**
     * Extract common thread pool information from Jetty's QueuedThreadPool.
     */
    private JettyThreadPoolInfo extractJettyThreadPoolInfo(QueuedThreadPool jettyExecutor) {
        int corePoolSize = jettyExecutor.getMinThreads();
        int maximumPoolSize = jettyExecutor.getMaxThreads();
        int activeCount = jettyExecutor.getBusyThreads();
        int currentPoolSize = jettyExecutor.getThreads();
        long keepAliveTimeSeconds = jettyExecutor.getIdleTimeout() * 1000L;

        BlockingQueue<?> jobs = (BlockingQueue<?>) ReflectUtil.getFieldValue(jettyExecutor, "_jobs");
        int blockingQueueSize = jettyExecutor.getQueueSize();
        int remainingCapacity = jobs.remainingCapacity();
        int queueCapacity = blockingQueueSize + remainingCapacity;
        String rejectedExecutionHandlerName = "JettyRejectedExecutionHandler";

        return new JettyThreadPoolInfo(
                corePoolSize,
                maximumPoolSize,
                activeCount,
                currentPoolSize,
                keepAliveTimeSeconds,
                jobs.getClass().getSimpleName(),
                blockingQueueSize,
                remainingCapacity,
                queueCapacity,
                rejectedExecutionHandlerName
        );
    }

    @Override
    public WebContainerEnum getWebContainerType() {
        return WebContainerEnum.JETTY;
    }

    /**
     * Simple DTO holding Jetty thread pool details for reuse across metrics/snapshot.
     */
    private record JettyThreadPoolInfo(
            int corePoolSize,
            int maximumPoolSize,
            int activeCount,
            int currentPoolSize,
            long keepAliveTimeSeconds,
            String queueName,
            int queueSize,
            int remainingCapacity,
            int queueCapacity,
            String rejectedHandlerName
    ) {}
}
