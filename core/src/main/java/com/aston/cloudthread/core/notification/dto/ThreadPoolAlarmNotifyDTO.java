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
package com.aston.cloudthread.core.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.function.Supplier;


/**
 * Thread pool runtime alarm notify entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ThreadPoolAlarmNotifyDTO {
    /**
     * Thread Pool unique id (UID)
     */
    private String threadPoolUID;

    /**
     * Application Name
     */
    private String applicationName;

    /**
     * Application active profile
     */
    private String activeProfile;

    /**
     * Application identify (App UID)
     */
    private String identify;

    /**
     * Alarm subscriber list in string, split by ','
     */
    private String subscribers;

    /**
     * Alarm type: Capacity, Activity, Reject
     */
    private String alarmType;

    /**
     * Core thread size in thread pool
     */
    private Integer corePoolSize;

    /**
     * Maximum thread size that thread pool can hold
     */
    private Integer maximumPoolSize;

    /**
     * Thread pool current thread size
     */
    private Integer currentPoolSize;

    /**
     * Thread pool inner active thread size
     */
    private Integer activePoolSize;

    /**
     * Maximum threads that thread pool can hold
     */
    private Integer largestPoolSize;

    /**
     * Thread pool total task count
     */
    private Long completedTaskCount;

    /**
     * Thread pool block queue type
     */
    private String workQueueName;

    /**
     * Thread pool working queue capacity
     */
    private Integer workQueueCapacity;

    /**
     * Thread pool working queue size
     */
    private Integer workQueueSize;

    /**
     * Thread pool working queue remaining capacity
     */
    private Integer workQueueRemainingCapacity;

    /**
     * Thread pool bind reject policy name
     */
    private String rejectedHandlerName;

    /**
     * Thread pool execute reject policy handler times
     */
    private Long rejectCount;

    /**
     * Current timestamp
     */
    private String currentTime;

    /**
     * Alarm interval, in minute
     */
    private Integer intervalMinutes;

    @ToString.Exclude
    private transient Supplier<ThreadPoolAlarmNotifyDTO> supplier;

    public ThreadPoolAlarmNotifyDTO resolve() {
        return supplier != null ? supplier.get() : this;
    }
}
