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

import com.aston.cloudthread.core.config.ApplicationProperties;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorWrapper;
import com.aston.cloudthread.core.executor.support.RejectedPolicyTypeEnum;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ThreadPoolMonitorTest {
    private ThreadPoolMonitor tPMonitor;

    @BeforeEach
    void setup() {
        tPMonitor = new ThreadPoolMonitor();

        BootstrapConfigProperties.getInstance().getMonitorConfig().setEnable(true);
        BootstrapConfigProperties.getInstance().getMonitorConfig().setCollectType("tag");
        BootstrapConfigProperties.getInstance().getMonitorConfig().setCollectIntervalSeconds(1L);
    }

    @AfterEach
    void teardown() {
        tPMonitor.stop();
    }

    private CloudThreadExecutor newExecutor(int queueCapacity,
                                            RejectedExecutionHandler handler,
                                            long awaitTerminationMillis) {
        return new CloudThreadExecutor(
                "test-pool",
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

    @Test
    void testLogMonitor_nullContext() {
        // this should warn
        tPMonitor.logMonitor(null);
    }

    @Test
    void testLogMonitor_validContext() {
        tPMonitor.setMicrometerMonitorCache(new HashMap<>());
        tPMonitor.setScheduler(new ScheduledThreadPoolExecutor(1));
        ThreadPoolRuntimeContext ctx = ThreadPoolRuntimeContext.builder()
                .threadPoolUID(UUID.randomUUID().toString())
                .corePoolSize(2)
                .maximumPoolSize(4)
                .build();
        tPMonitor.logMonitor(ctx); // should info here
    }

    @Test
    void testMicrometerMonitor_addsNewContext() {
        try (MockedStatic<ApplicationProperties> appPropsMocked = mockStatic(ApplicationProperties.class)) {
            tPMonitor.setMicrometerMonitorCache(new HashMap<>());
            tPMonitor.setScheduler(new ScheduledThreadPoolExecutor(1));
            appPropsMocked.when(ApplicationProperties::getApplicationName)
                    .thenReturn("app-" + UUID.randomUUID());
            appPropsMocked.when(ApplicationProperties::getActiveProfile)
                    .thenReturn("test");
            String UID = UUID.randomUUID().toString();
            ThreadPoolRuntimeContext ctx = ThreadPoolRuntimeContext.builder()
                    .threadPoolUID(UID)
                    .maximumPoolSize(4)
                    .corePoolSize(2)
                    .build();

            tPMonitor.micrometerMonitor(ctx);
            assertTrue(tPMonitor.getMicrometerMonitorCache().containsKey(UID));
            assertEquals(2, tPMonitor.getMicrometerMonitorCache().get(UID).getCorePoolSize());
        }
    }

    @Test
    void testMicrometerMonitor_updatesExistingContext() {
        try (MockedStatic<ApplicationProperties> appPropsMocked = mockStatic(ApplicationProperties.class)) {

            tPMonitor.setMicrometerMonitorCache(new HashMap<>());
            tPMonitor.setScheduler(new ScheduledThreadPoolExecutor(1));

            appPropsMocked.when(ApplicationProperties::getApplicationName)
                    .thenReturn("app-" + UUID.randomUUID());
            appPropsMocked.when(ApplicationProperties::getActiveProfile)
                    .thenReturn("test");
            String UID = UUID.randomUUID().toString();

            ThreadPoolRuntimeContext ctx1 = ThreadPoolRuntimeContext.builder()
                    .threadPoolUID(UID)
                    .corePoolSize(2)
                    .maximumPoolSize(4)
                    .build();
            tPMonitor.micrometerMonitor(ctx1);

            ThreadPoolRuntimeContext ctx2 = ThreadPoolRuntimeContext.builder()
                    .threadPoolUID(UID)
                    .corePoolSize(5)
                    .maximumPoolSize(10)
                    .build();
            tPMonitor.micrometerMonitor(ctx2);

            assertEquals(tPMonitor.getMicrometerMonitorCache().get(UID).getCorePoolSize(), 5);
            assertEquals(tPMonitor.getMicrometerMonitorCache().get(UID).getMaximumPoolSize(), 10);
        }
    }

    @Test
    @SneakyThrows
    void testStart_andStopScheduler() {
        tPMonitor.start();
        assertNotNull(tPMonitor.getScheduler());
        assertFalse(tPMonitor.getScheduler().isShutdown());

        // wait a bit for scheduled task to execute
        Thread.sleep(1000L);

        tPMonitor.stop();
        assertTrue(tPMonitor.getScheduler().isShutdown());
    }

    @Test
    void testBuildThreadPoolRuntimeContext() {
        CloudThreadExecutor executor = newExecutor(1, new ThreadPoolExecutor.AbortPolicy(),
                1000L);
        ThreadPoolExecutorWrapper wrapper = mock(ThreadPoolExecutorWrapper.class);
        when(wrapper.getExecutor()).thenReturn(executor);
        when(wrapper.getThreadPoolUID()).thenReturn(executor.getThreadPoolUID());

        ThreadPoolRuntimeContext ctx = tPMonitor.buildThreadPoolRuntimeContext(wrapper);
        assertEquals(ctx.getThreadPoolUID(), executor.getThreadPoolUID());
        assertEquals(ctx.getMaximumPoolSize(), executor.getMaximumPoolSize());
        assertEquals(ctx.getCorePoolSize(), executor.getCorePoolSize());
        assertEquals(ctx.getRejectedHandlerName(),
                RejectedPolicyTypeEnum.ABORT_POLICY.getName());
    }
}