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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thread pool runtime context metadata holder class
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPoolRuntimeContext {
    /**
     * Thread pool context Unique Id (UID)
     */
    private String threadPoolUID;

    /**
     * Thread pool configured inner core thread size
     */
    private Integer corePoolSize;

    /**
     * Thread pool maximum threads can be hold
     */
    private Integer maximumPoolSize;

    /**
     * Thread pool current active thread number
     */
    private Integer currentPoolSize;

    /**
     * Thread pool active thread number
     */
    private Integer activePoolSize;

    /**
     * Largest pool size of thread pool
     */
    private Integer largestPoolSize;

    /**
     * Thread pool completed task counter
     */
    private Long completedTaskCount;

    /**
     * Thread pool block queue type
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
     * Thread pool worker queue remaining capacity
     */
    private Integer workQueueRemainingCapacity;

    /**
     * Thread pool configured reject policy
     */
    private String rejectedHandlerName;

    /**
     * Thread pool reject policy handler invoke time counter
     */
    private Long rejectCount;
}
