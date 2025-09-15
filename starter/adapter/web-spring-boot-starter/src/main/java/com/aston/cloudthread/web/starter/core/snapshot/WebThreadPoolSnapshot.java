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
package com.aston.cloudthread.web.starter.core.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Snapshot of thread pool runtime context (lock supported, no high frequency invocation
 * recommended)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebThreadPoolSnapshot {
    /**
     * Core thread number in the pool
     */
    private Integer corePoolSize;

    /**
     * Maximum allowed number of threads in the pool
     */
    private Integer maximumPoolSize;

    /**
     * Current size of the pool
     */
    private Integer currentPoolSize;

    /**
     * Active thread number in the pool
     */
    private Integer activePoolSize;

    /**
     * largest size of the pool
     */
    private Integer largestPoolSize;

    /**
     * Thread pool inner idle thread alive time in seconds
     */
    private Long keepAliveTimeSeconds;

    /**
     * Thread pool completed task count
     */
    private Long completedTaskCount;

    /**
     * Thread pool work queue name
     */
    private String workQueueName;

    /**
     * Thread pool work queue capacity
     */
    private Integer workQueueCapacity;

    /**
     * Thread pool work queue size
     */
    private Integer workQueueSize;

    /**
     * Thread pool work queue remaining queue capacity
     */
    private Integer workQueueRemainingCapacity;

    /**
     * Thread pool rejection policy name
     */
    private String rejectedHandlerName;
}
