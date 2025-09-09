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

import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadPoolMonitorIT {

    private ThreadPoolMonitor monitor;

    // @BeforeEach
    void setup() {
        CloudThreadRegistry.getAllWrappers().forEach(wrapper ->
                CloudThreadRegistry.getWrapper(wrapper.getThreadPoolUID()));
        monitor = new ThreadPoolMonitor();
        monitor.start();
    }

    // @Test
    void testThreadPoolRegistrationAndMonitorCache() {
        String poolId = "test-pool";
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 2, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
        ThreadPoolExecutorProperties props = new ThreadPoolExecutorProperties();
        props.setCoolPoolSize(1);
        props.setMaximumPoolSize(2);

        // register the executor
        CloudThreadRegistry.putWrapper(poolId, executor, props);

        // verify registry retrieval
        ThreadPoolExecutorWrapper wrapper = CloudThreadRegistry.getWrapper(poolId);
        assertNotNull(wrapper);
        assertEquals(poolId, wrapper.getThreadPoolUID());
        assertEquals(executor, wrapper.getExecutor());

        // wait for monitor to pick up thread pool and update micrometer cache
        await().atMost(Duration.ofSeconds(5))
                .until(() -> monitor.getMicrometerMonitorCache().containsKey(poolId));

        assertTrue(monitor.getMicrometerMonitorCache().containsKey(poolId));

        // cleanup
        executor.shutdownNow();
        monitor.stop();
    }
}