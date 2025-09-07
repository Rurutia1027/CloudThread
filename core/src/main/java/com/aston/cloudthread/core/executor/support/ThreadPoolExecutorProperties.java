package com.aston.cloudthread.core.executor.support;

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
