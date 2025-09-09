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
package com.aston.cloudthread.core.toolkit;

import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadPoolExecutorBuilderTest {

    private ThreadFactory threadFactory;

    @BeforeEach
    void setup() {
        threadFactory = Executors.defaultThreadFactory();
    }

    @Test
    void testBuildWithDefaults_shouldThrowExceptionWithoutThreadFactory() {
        ThreadPoolExecutorBuilder builder = ThreadPoolExecutorBuilder.builder();
        Assertions.assertThrows(IllegalArgumentException.class,
                builder::build,
                "ThreadFactory is required, should not allow null");
    }

    @Test
    void testBuildNormalThreadPool() {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("testPool")
                .corePoolSize(2)
                .maximumPoolSize(4)
                .workQueueType(BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE)
                .workQueueCapacity(10)
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .threadFactory(threadFactory)
                .keepAliveTimeSeconds(5L)
                .allowCoreThreadTimeOut(true)
                .awaitTerminationMillis(2000L)
                .build();

        assertNotNull(executor);
        assertEquals(2, executor.getCorePoolSize());
        assertEquals(4, executor.getMaximumPoolSize());
        assertEquals(5L, executor.getKeepAliveTime(TimeUnit.SECONDS));
        assertTrue(executor.allowsCoreThreadTimeOut());
        assertTrue(executor.getRejectedExecutionHandler() instanceof ThreadPoolExecutor.CallerRunsPolicy);

        executor.shutdown();
    }

    @Test
    void testBuildDynamicThreadPool() {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("dynamicPool")
                .corePoolSize(1)
                .maximumPoolSize(2)
                .dynamicPool()
                .threadFactory(threadFactory)
                .build();

        assertNotNull(executor);
        assertTrue(executor instanceof CloudThreadExecutor, "DynamicPool should return CloudThreadExecutor");
        executor.shutdown();
    }

    @Test
    void testDefaultRejectedHandlerShouldBeAbortPolicy() {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("defaultRejectHandler")
                .threadFactory(threadFactory)
                .maximumPoolSize(3)
                .corePoolSize(2)
                .keepAliveTimeSeconds(1000L)
                .build();

        assertNotNull(executor.getRejectedExecutionHandler());
        assertTrue(executor.getRejectedExecutionHandler() instanceof ThreadPoolExecutor.AbortPolicy);
        executor.shutdown();
    }

    @Test
    void testThreadFactoryByNamePrefix() {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("prefixPool")
                .threadFactory("cloudThread-")
                .corePoolSize(3)
                .maximumPoolSize(8)
                .keepAliveTimeSeconds(3000L)
                .build();

        Thread t = executor.getThreadFactory().newThread(() -> {
        });
        assertTrue(t.getName().startsWith("cloudThread-"));
        executor.shutdown();
    }

    @Test
    void testThreadFactoryByNamePrefixWithDaemon() {
        ThreadPoolExecutor executor = ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("daemonPool")
                .threadFactory("cloudThread-", true)
                .keepAliveTimeSeconds(1000L)
                .maximumPoolSize(4)
                .corePoolSize(2)
                .build();

        Thread t = executor.getThreadFactory().newThread(() -> {
        });
        assertTrue(t.isDaemon(), "Thread should be daemon");
        executor.shutdown();
    }

}