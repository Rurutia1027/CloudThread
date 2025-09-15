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
package com.aston.cloudthread.web.starter.core.metric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metrics of Web Servlet Container's thread pool
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebThreadPoolMetrics {
    /**
     * Web container name
     */
    private String webContainerName;

    /**
     * Web container network address info
     */
    private String networkAddress;

    /**
     * Web container corresponding spring app active profile
     */
    private String activeProfile;

    /**
     * Web container inner bind thread pool core thread number
     */
    private Integer corePoolSize;

    /**
     * Web container inner bind thread pool maximum thread number
     */
    private Integer maximumPoolSize;

    /**
     * Web container inner bind thread pool inner idle thread keep alive time in seconds
     */
    private Long keepAliveTimeSeconds;

    /**
     * Web container bind thread pool work queue type
     */
    private String workQueueName;

    /**
     * Web container bind thread pool work queue capacity
     */
    private Integer workQueueCapacity;

    /**
     * Web container bind thread pool work queue size
     */
    private Integer workQueueSize;

    /**
     * Web container bind thread pool work queue remaining capacity
     */
    private Integer workQueueRemainingCapacity;

    /**
     * Web container bind thread pool adopted reject policy name
     */
    private String rejectedHandlerName;
}
