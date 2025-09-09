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

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class CloudThreadRegistryTest {
    private CloudThreadExecutor executor1;
    private CloudThreadExecutor executor2;
    private ThreadPoolExecutorProperties props1;
    private ThreadPoolExecutorProperties props2;

    private int queueCapacity = 2;
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
    private long awaitTerminationMillis = 1000L;

    private CloudThreadExecutor newExecutor(String tUID, int queueCapacity,
                                            RejectedExecutionHandler handler,
                                            long awaitTerminationMillis) {
        return new CloudThreadExecutor(
                tUID,
                1,
                1,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                Executors.defaultThreadFactory(),
                handler,
                awaitTerminationMillis
        );
    }

    @BeforeEach
    void setup() {

        executor1 = newExecutor(UUID.randomUUID().toString(), queueCapacity, handler,
                awaitTerminationMillis);
        executor2 = newExecutor(UUID.randomUUID().toString(), queueCapacity, handler,
                awaitTerminationMillis);

        props1 = ThreadPoolExecutorProperties.builder()
                .threadPoolId("pool-1")
                .coolPoolSize(1)
                .maximumPoolSize(1)
                .queueCapacity(2)
                .workingQueue("ArrayBlockingQueue")
                .rejectedHandler("AbortPolicy")
                .keeAliveTime(1L)
                .allowCoreThreadTimeout(false)
                .notifyConfig(new ThreadPoolExecutorProperties.NotifyConfig("dev-team", 10L))
                .alertConfig(new ThreadPoolExecutorProperties.AlertConfig(true, 80, 80))
                .build();

        props2 = ThreadPoolExecutorProperties.builder()
                .threadPoolId("pool-2")
                .coolPoolSize(2)
                .maximumPoolSize(4)
                .queueCapacity(2)
                .workingQueue("ArrayBlockingQueue")
                .rejectedHandler("AbortPolicy")
                .keeAliveTime(1L)
                .allowCoreThreadTimeout(true)
                .notifyConfig(new ThreadPoolExecutorProperties.NotifyConfig("teamB", 10L))
                .alertConfig(new ThreadPoolExecutorProperties.AlertConfig(true, 70, 70))
                .build();
    }

    @AfterEach
    void teardown() {
        CloudThreadRegistry.clear();
    }

    @Test
    void testPutAndGetWrapper() {
        CloudThreadRegistry.putWrapper(executor1.getThreadPoolUID(), executor1, props1);
        ThreadPoolExecutorWrapper wrapper = CloudThreadRegistry.getWrapper(executor1.getThreadPoolUID());
        Assertions.assertNotNull(wrapper);
        Assertions.assertEquals(executor1, wrapper.getExecutor());
        Assertions.assertEquals(props1, wrapper.getExecutorProperties());
        Assertions.assertEquals(executor1.getThreadPoolUID(), wrapper.getThreadPoolUID());
    }

    @Test
    void testGetWrapperNotFound() {
        Assertions.assertNull(CloudThreadRegistry.getWrapper("non-existent"));
        // ret empty list
        Assertions.assertNotNull(CloudThreadRegistry.getAllWrappers());
    }

    @Test
    void testOverwriteWrapper() {
        CloudThreadRegistry.putWrapper("pool-duplicate", executor1, props1);
        CloudThreadRegistry.putWrapper("pool-duplicate", executor2, props2);

        ThreadPoolExecutorWrapper wrapper = CloudThreadRegistry.getWrapper("pool-duplicate");

        Assertions.assertNotNull(wrapper);
        Assertions.assertEquals("pool-duplicate", wrapper.getThreadPoolUID());
        Assertions.assertEquals(executor2, wrapper.getExecutor());
        Assertions.assertEquals(props2, wrapper.getExecutorProperties());
    }

    @Test
    void testGetAllWrappers() {
        CloudThreadRegistry.putWrapper(executor1.getThreadPoolUID(), executor1, props1);
        CloudThreadRegistry.putWrapper(executor2.getThreadPoolUID(), executor2, props2);

        Collection<ThreadPoolExecutorWrapper> wrappers = CloudThreadRegistry.getAllWrappers();
        Assertions.assertEquals(2, wrappers.size());
    }

    @Test
    @SneakyThrows
    void testThreadSafety() {
        int threadCount = 20;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> {
                // in thread run func
                // we create executor instances, thread pool properties
                // and put them to the cache as pair
                CloudThreadExecutor executor = newExecutor(
                        "pool-" + idx,
                        queueCapacity,
                        handler,
                        awaitTerminationMillis
                );

                ThreadPoolExecutorProperties props =
                        ThreadPoolExecutorProperties.builder()
                                .coolPoolSize(1)
                                .threadPoolId(executor.getThreadPoolUID())
                                .allowCoreThreadTimeout(true)
                                .maximumPoolSize(1)
                                .build();
                CloudThreadRegistry.putWrapper(executor.getThreadPoolUID(), executor, props);
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        Collection<ThreadPoolExecutorWrapper> wrappers = CloudThreadRegistry.getAllWrappers();
        Assertions.assertTrue(wrappers.size() >= threadCount);
    }

}