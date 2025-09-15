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
package com.aston.cloudthread.web.starter.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration model for CloudThread integration with Web Server thread pools.
 *
 * <p>
 * This class represents the dynamic thread pool parameters used when binding
 * CloudThread to servlet containers such as
 * Tomcat, Jetty, or Undertow.
 * </p>
 * <p>
 * The configuration values are typically sources from external configuration (e.g.,
 * Spring Boot properties, config centers, or Kubernetes ConfigMaps) and may be
 * refreshed at runtime to adapt to workload changes.
 * </p>
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebThreadPoolConfig {
    /**
     * Core number of threads maintained in the pool.
     */
    private Integer corePoolSize;

    /**
     * Maximum allowed number of threads in the pool.
     */
    private Integer maximumPoolSize;

    /**
     * Time (in seconds) that excess idle threads will wait for new tasks before terminating.
     */
    private Long keepAliveTimeSeconds;
}
