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
package com.aston.cloudthread.core.executor.support;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Enumeration of thread pool rejection policies.
 * <p>
 * This enum provides a mapping between a human-readable policy name and the
 * corresponding {@link RejectedExecutionHandler} implementation
 * used by {@link ThreadPoolExecutor} when tasks are rejected.
 * <p>
 * Available policies include:
 * <ul>
 *    <li>{@link ThreadPoolExecutor.CallerRunsPolicy}</li>
 *    <li>{@link ThreadPoolExecutor.AbortPolicy}</li>
 *    <li>{@link ThreadPoolExecutor.DiscardPolicy}</li>
 *    <li>{@link ThreadPoolExecutor.DiscardOldestPolicy}</li>
 * </ul>
 *
 * <p>
 * The enum also provides a utility method {@link #createPolicy(String)} to instantiate the
 * appropriate {@link RejectedExecutionHandler} based on a policy name string.
 */
public enum RejectedPolicyTypeEnum {
    /**
     * {@link ThreadPoolExecutor.CallerRunsPolicy}
     */
    CALLER_RUNS_POLICY("CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy()),

    /**
     * {@link ThreadPoolExecutor.AbortPolicy}
     */
    ABORT_POLICY("AbortPolicy", new ThreadPoolExecutor.AbortPolicy()),

    /**
     * {@link ThreadPoolExecutor.DiscardPolicy}
     */
    DISCARD_POLICY("DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy()),

    /**
     * {@link ThreadPoolExecutor.DiscardOldestPolicy}
     */
    DISCARD_OLDEST_POLICY("DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy());

    @Getter
    private String name;

    @Getter
    private RejectedExecutionHandler rejectedHandler;

    RejectedPolicyTypeEnum(String rejectedPolicyName, RejectedExecutionHandler rejectedHandler) {
        this.name = rejectedPolicyName;
        this.rejectedHandler = rejectedHandler;
    }

    private static final Map<String, RejectedPolicyTypeEnum> NAME_TO_ENUM_MAP;

    static {
        final RejectedPolicyTypeEnum[] values = RejectedPolicyTypeEnum.values();
        NAME_TO_ENUM_MAP = new HashMap<>(values.length);
        for (RejectedPolicyTypeEnum value : values) {
            NAME_TO_ENUM_MAP.put(value.name, value);
        }
    }

    /**
     * Creates a {@link RejectedExecutionHandler} based on the given
     * {@link RejectedPolicyTypeEnum#name RejectedPolicyTypeEnum.name}.
     *
     * @param rejectedPolicyName the {@link RejectedPolicyTypeEnum#name RejectedPolicyTypeEnum.name}
     * @return the corresponding {@link RejectedExecutionHandler} instance
     * @throws IllegalArgumentException if no matching rejected policy type is found
     */
    public static RejectedExecutionHandler createPolicy(String rejectedPolicyName) {
        RejectedPolicyTypeEnum rejectedPolicyTypeEnum = NAME_TO_ENUM_MAP.get(rejectedPolicyName);
        if (rejectedPolicyTypeEnum != null) {
            return rejectedPolicyTypeEnum.rejectedHandler;
        }

        throw new IllegalArgumentException("No matching type of rejected execution was found: " + rejectedPolicyName);
    }
}
