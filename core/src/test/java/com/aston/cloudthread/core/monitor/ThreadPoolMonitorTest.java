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

import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ThreadPoolMonitorTest {
    private ThreadPoolMonitor monitor;

    @BeforeEach
    void setUp() {
        monitor = new ThreadPoolMonitor();
        monitor.start();
        BootstrapConfigProperties.getInstance().getMonitorConfig().setEnable(true);
        BootstrapConfigProperties.getInstance().getMonitorConfig().setCollectType("log");
        BootstrapConfigProperties.getInstance().getMonitorConfig().setCollectIntervalSeconds(1L);
    }

    @AfterEach
    void tearDown() {
        monitor.stop();
    }

    @Test
    void testLogMonitor_nullContext() {
        monitor.logMonitor(null); // should log warning but not throw
    }

    @Test
    void testLogMonitor_validContext() {
        ThreadPoolRuntimeContext ctx = ThreadPoolRuntimeContext.builder()
                .threadPoolUID("test-pool")
                .corePoolSize(2)
                .maximumPoolSize(4)
                .build();
        monitor.logMonitor(ctx); // should log info, not throw
    }

   // @Test
    void testMicrometerMonitor_addsNewContext() {
        ThreadPoolRuntimeContext ctx = ThreadPoolRuntimeContext.builder()
                .threadPoolUID("pool-1")
                .corePoolSize(2)
                .maximumPoolSize(4)
                .build();

        monitor.micrometerMonitor(ctx);

        assertTrue(monitor.getMicrometerMonitorCache().containsKey("pool-1"));
        assertEquals(2, monitor.getMicrometerMonitorCache().get("pool-1").getCorePoolSize());
    }

    // @Test
    void testMicrometerMonitor_updatesExistingContext() {
        ThreadPoolRuntimeContext ctx1 = ThreadPoolRuntimeContext.builder()
                .threadPoolUID("pool-1")
                .corePoolSize(2)
                .maximumPoolSize(4)
                .build();

        monitor.micrometerMonitor(ctx1);

        ThreadPoolRuntimeContext ctx2 = ThreadPoolRuntimeContext.builder()
                .threadPoolUID("pool-1")
                .corePoolSize(5)
                .maximumPoolSize(10)
                .build();

        monitor.micrometerMonitor(ctx2);

        assertEquals(5, monitor.getMicrometerMonitorCache().get("pool-1").getCorePoolSize());
        assertEquals(10, monitor.getMicrometerMonitorCache().get("pool-1").getMaximumPoolSize());
    }

    @Test
    void testStart_andStopScheduler() throws InterruptedException {
        monitor.start();
        assertNotNull(monitor.getScheduler());
        assertFalse(monitor.getScheduler().isShutdown());

        // wait a bit for scheduled task to execute
        Thread.sleep(1500);

        monitor.stop();
        assertTrue(monitor.getScheduler().isShutdown());
    }

    @Test
    void testBuildThreadPoolRuntimeContext() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4,
                10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());

        ThreadPoolExecutorWrapper wrapper = mock(ThreadPoolExecutorWrapper.class);
        when(wrapper.getExecutor()).thenReturn(executor);
        when(wrapper.getThreadPoolUID()).thenReturn("pool-x");

        ThreadPoolRuntimeContext ctx = monitor.buildThreadPoolRuntimeContext(wrapper);

        assertEquals("pool-x", ctx.getThreadPoolUID());
        assertEquals(2, ctx.getCorePoolSize());
        assertEquals(4, ctx.getMaximumPoolSize());
        assertEquals("LinkedBlockingQueue", ctx.getWorkQueueName());
    }
}