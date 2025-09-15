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

import com.aston.cloudthread.core.constant.Constants;
import com.aston.cloudthread.web.starter.core.config.WebThreadPoolConfig;
import com.aston.cloudthread.web.starter.core.enums.WebContainerEnum;
import com.aston.cloudthread.web.starter.core.metric.WebThreadPoolMetrics;
import com.aston.cloudthread.web.starter.core.snapshot.WebThreadPoolSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TomcatWebThreadPoolService extends AbstractWebThreadPoolService{
    @Override
    protected Executor getWebServerInnerExecutor(WebServer webServer) {
        return ((TomcatWebServer) webServer).getTomcat()
                .getConnector()
                .getProtocolHandler()
                .getExecutor();
    }

    @Override
    public void updateThreadPool(WebThreadPoolConfig config) {
        try {
            ThreadPoolExecutor tomcatExecutor = (ThreadPoolExecutor) webExecutorRef;
            int originalCorePoolSize = tomcatExecutor.getCorePoolSize();
            int originalMaximumPoolSize = tomcatExecutor.getMaximumPoolSize();
            long originalKeepAliveTimeSecs = tomcatExecutor.getKeepAliveTime(TimeUnit.SECONDS);

            if (config.getCorePoolSize() > originalMaximumPoolSize) {
                tomcatExecutor.setMaximumPoolSize(config.getMaximumPoolSize());
                tomcatExecutor.setCorePoolSize(config.getCorePoolSize());
            } else {
                tomcatExecutor.setCorePoolSize(config.getCorePoolSize());
                tomcatExecutor.setMaximumPoolSize(config.getMaximumPoolSize());
            }
            tomcatExecutor.setKeepAliveTime(config.getKeepAliveTimeSeconds(), TimeUnit.SECONDS);

            log.info("[Tomcat] Changed web thread pool. corePoolSize: {}, maximumPoolSize: {}, keepAliveTime: {}",
                    String.format(Constants.CHANGE_DELIMITER, originalCorePoolSize, config.getCorePoolSize()),
                    String.format(Constants.CHANGE_DELIMITER, originalMaximumPoolSize, config.getMaximumPoolSize()),
                    String.format(Constants.CHANGE_DELIMITER, originalKeepAliveTimeSecs,
                            config.getKeepAliveTimeSeconds()));
        } catch (Exception ex) {
            log.error("Failed to modify the Tomcat thread pool parameter.", ex);
        }
    }

    @Override
    public WebThreadPoolMetrics getMetrics() {
        TomcatThreadPoolInfo info = extractTomcatThreadPoolInfo((ThreadPoolExecutor) webExecutorRef);

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
        TomcatThreadPoolInfo info = extractTomcatThreadPoolInfo((ThreadPoolExecutor) webExecutorRef);

        return WebThreadPoolSnapshot.builder()
                .corePoolSize(info.corePoolSize)
                .maximumPoolSize(info.maximumPoolSize)
                .activePoolSize(info.activeCount)
                .completedTaskCount(info.completedTaskCount)
                .largestPoolSize(info.largestPoolSize)
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
     * Extract common thread pool information from Tomcat's ThreadPoolExecutor.
     */
    private TomcatThreadPoolInfo extractTomcatThreadPoolInfo(ThreadPoolExecutor executor) {
        int corePoolSize = executor.getCorePoolSize();
        int maximumPoolSize = executor.getMaximumPoolSize();
        int activeCount = executor.getActiveCount();
        long completedTaskCount = executor.getCompletedTaskCount();
        int largestPoolSize = executor.getLargestPoolSize();
        int currentPoolSize = executor.getPoolSize();
        long keepAliveTimeSeconds = executor.getKeepAliveTime(TimeUnit.SECONDS);

        BlockingQueue<?> queue = executor.getQueue();
        int queueSize = queue.size();
        int remainingCapacity = queue.remainingCapacity();
        int queueCapacity = queueSize + remainingCapacity;
        String rejectedHandlerName = executor.getRejectedExecutionHandler().getClass().getSimpleName();

        return new TomcatThreadPoolInfo(
                corePoolSize,
                maximumPoolSize,
                activeCount,
                completedTaskCount,
                largestPoolSize,
                currentPoolSize,
                keepAliveTimeSeconds,
                queue.getClass().getSimpleName(),
                queueSize,
                remainingCapacity,
                queueCapacity,
                rejectedHandlerName
        );
    }

    @Override
    public WebContainerEnum getWebContainerType() {
        return WebContainerEnum.TOMCAT;
    }

    /**
     * Simple DTO holding Tomcat thread pool details for reuse across metrics/snapshot.
     */
    private record TomcatThreadPoolInfo(
            int corePoolSize,
            int maximumPoolSize,
            int activeCount,
            long completedTaskCount,
            int largestPoolSize,
            int currentPoolSize,
            long keepAliveTimeSeconds,
            String queueName,
            int queueSize,
            int remainingCapacity,
            int queueCapacity,
            String rejectedHandlerName
    ) {
    }
}
