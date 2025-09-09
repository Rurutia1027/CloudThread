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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Registry for managing dynamic thread pools in CloudThread.
 *
 * <p>This class acts as a centralized manager for all thread pool executors within an
 * application. Each thread pool is identified by a unique ID and stored together with its
 * configuration properties in a wrapper object.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *     <li>Register new thread pool executors with associated configuration.</li>
 *     <li>Provide lookup access to a specific thread pool by its ID.</li>
 *     <li>Expose all registered thread pools for monitoring and management. </li>
 * </ul>
 *
 * <p>Typical usage scenarios: </p>
 * <ul>
 *     <li>Centralizing lifecycle management of multiple executors. </li>
 *     <li>Supporting dynamic configuration updates at runtime.</li>
 *     <li>Providing hooks for monitoring, alarms, and observability.</li>
 * </ul>
 *
 * <p>This class is a core building block for enabling cloud-native, dynamically
 * configurable thread pools.</p>
 */
public class CloudThreadRegistry {

    /**
     * Cache for thread pool wrappers.
     * The key is the thread pool unique ID, and the value is the wrapper
     * {@link ThreadPoolExecutorWrapper} which encapsulates the executor
     * and its configuration properties.
     */
    private static final Map<String, ThreadPoolExecutorWrapper> WRAPPER_MAP =
            new ConcurrentHashMap<>();

    /**
     * Register a new thread pool into the registry by wrapping it together with
     * its configuration properties.
     *
     * @param threadPoolUID unique identifier for the thread pool
     * @param executor      the thread pool executor instance
     * @param properties    thread pool configuration properties
     */
    public static void putWrapper(String threadPoolUID,
                                  ThreadPoolExecutor executor,
                                  ThreadPoolExecutorProperties properties) {
        ThreadPoolExecutorWrapper executorWrapper =
                new ThreadPoolExecutorWrapper(threadPoolUID, executor, properties);
        WRAPPER_MAP.put(threadPoolUID, executorWrapper);
    }

    /**
     * Retrieve a specific thread pool wrapper by its unique ID.
     *
     * @param threadPoolUID unique identifier for the thread pool
     * @return the corresponding {@link ThreadPoolExecutorWrapper}, or {@code null}
     *         if no thread pool is registered with the given ID
     */
    public static ThreadPoolExecutorWrapper getWrapper(String threadPoolUID) {
        return WRAPPER_MAP.getOrDefault(threadPoolUID, null);
    }

    /**
     * Retrieve all registered thread pool wrappers.
     *
     * @return a collection of {@link ThreadPoolExecutorWrapper} instances
     *         currently managed by the registry
     */
    public static Collection<ThreadPoolExecutorWrapper> getAllWrappers() {
        return WRAPPER_MAP.values();
    }

    public static void clear() {
        WRAPPER_MAP.clear();
    }
}