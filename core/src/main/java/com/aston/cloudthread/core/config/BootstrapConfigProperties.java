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
package com.aston.cloudthread.core.config;

import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import com.aston.cloudthread.core.parser.ConfigFileTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Properties of CloudThread config center
 */
@Data
public class BootstrapConfigProperties {
    public static final String PREFIX = "cloudthread";

    /**
     * Enable dynamic thread pool
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * Config files for Spring Cloud Config
     */
    private SpringCloudConfig springCloudConfig;

    /**
     * Config files for Consul Config
     */
    private ConsulConfig consulConfig;

    /**
     * AwsConfig
     */
    private AwsConfig awsConfig;

    /**
     * Web thread pool config
     */
    private WebThreadPoolExecutorConfig webConfig;

    /**
     * Spring Cloud Config in yaml file format
     */
    private ConfigFileTypeEnum configFileType;

    /**
     * Notification platforms config
     */
    private NotifyPlatformsConfig notifyPlatforms;

    /**
     * Monitor config
     */
    private MonitorConfig monitorConfig = new MonitorConfig();


    // --- static classes for configs ---
    @Data
    public static class SpringCloudConfig {
        /**
         * Name fo the Spring Cloud configuration application.
         */
        private String applicationName;

        /**
         * Profile to load configuration from (e.g., "dev", "prod")
         */
        private String profile;

        /**
         * Label or branch in the config repository.
         */
        private String label;
    }

    @Data
    public static class ConsulConfig {
        /**
         * Consul key prefix or service name
         */
        private String serviceName;

        /**
         * Consul datacenter (optional)
         */
        private String datacenter;

        /**
         * Consul configuration path or key
         */
        private String keyPrefix;
    }

    /**
     * cloudthread:
     *   aws-config:
     *     region: us-east-1
     *     profile: my-dev-profile
     *     parameter-keys:
     *       - /cloudthread/producerPool/corePoolSize
     *       - /cloudthread/consumerPool/maximumPoolSize
     *     refresh-interval-seconds: 60
     */
    @Data
    public static class AwsConfig {
        /**
         * AWS region, e.g., "us-east-1"
         */
        private String region;

        /**
         * AWS credentials profile name (optional)
         */
        private String profile;

        /**
         * List of parameter keys to fetch from SSM/AppConfig
         */
        private List<String> parameterKeys;

        /**
         * Optional refresh interval in seconds
         */
        private long refreshIntervalSeconds = 60;
    }

    @Data
    public static class MonitorConfig {
        /**
         * Enable monitor, true: enabled, default: enabled
         */
        private Boolean enable = Boolean.TRUE;

        /**
         * Monitor type
         */
        private String collectType = "micrometer";

        /**
         * Metric collect intervals in Seconds
         */
        private long collectIntervalSeconds = 10L;
    }

    @Data
    public static class WebThreadPoolExecutorConfig {
        /**
         * Thread pool core thread number
         */
        private Integer corePoolSize;

        /**
         * Thread pool maximum thread number
         */
        private Integer maximumPoolSize;

        /**
         * Alive time in MS in thread pool for idle threads
         */
        private Long keepAliveTimeSeconds;

        /**
         * Notification config
         */
        private NotifyConfig notifyConfig;
    }

    /**
     * Thread pool properties collection
     */
    private List<ThreadPoolExecutorProperties> executors;

    @Data
    public static class NotifyPlatformsConfig {

        /**
         * Notification platform, discord, slack, teams
         */
        private String platform;

        /**
         * Notification web hook url address
         */
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotifyConfig {
        /**
         * Notification subscribers in string list, split by ','
         */
        private String subscribers;


        /**
         * Notification intervals in seconds.
         */
        private Long intervals = 5L;
    }

    // --- bootstrap config props instance ----
    private static BootstrapConfigProperties INSTANCE = new BootstrapConfigProperties();

    public static BootstrapConfigProperties getInstance() {
        return INSTANCE;
    }

    public static void setInstance(BootstrapConfigProperties properties) {
        INSTANCE = properties;
    }
}
