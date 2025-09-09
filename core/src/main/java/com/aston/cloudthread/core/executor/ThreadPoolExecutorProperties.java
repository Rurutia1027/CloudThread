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
package com.aston.cloudthread.core.executor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Thread pool properties
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ThreadPoolExecutorProperties {
    /**
     * Global unique ID for thread pool.
     */
    private String threadPoolId;

    /**
     * Core thread pool size.
     */
    private Integer coolPoolSize;

    /**
     * Maximum of thread pool size.
     */
    private Integer maximumPoolSize;

    /**
     * Queue capacity.
     */
    private Integer queueCapacity;

    /**
     * Block queue type.
     */
    private String workingQueue;

    /**
     * Reject strategy type.
     */
    private String rejectedHandler;

    /**
     * Thread idle alive time in seconds.
     */
    private Long keeAliveTime;

    /**
     * Is core thread timeout allowed?
     */
    private Boolean allowCoreThreadTimeout;

    /**
     * Notification configuration.
     */
    private NotifyConfig notifyConfig;

    /**
     * Alert configuration.
     */
    private AlertConfig alertConfig;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotifyConfig {
        /**
         * Notification subscribers collection.
         */
        private String subscribers;


        /**
         * Notification intervals in seconds.
         */
        private Long intervals = 5L;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertConfig {
        /**
         * Alert enabled in default or not.
         */
        private Boolean enable = Boolean.TRUE;

        /**
         * Queue threshold.
         */
        private Integer queueThreshold = 80;

        /**
         * Alive thread threshold.
         */
        private Integer activeThreadThreshold = 80;
    }
}
