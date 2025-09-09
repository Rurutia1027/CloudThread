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

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CloudThreadExecutorTest {
    private CloudThreadExecutor executor;

    @AfterEach
    void tearDown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
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
    @SneakyThrows
    void testBasicTaskExecution() {
        executor = newExecutor(10, new ThreadPoolExecutor.AbortPolicy(), 1000);
        Future<String> future = executor.submit(() -> "hello");
        assertEquals("hello", future.get());
    }

    @Test
    void testRejectPolicyIncrementsRejectCount() {
        // Pool = 1 core thread, max=1, queue capacity=1
        // => at most 1 running + 1 queued, everything else rejected.
        executor = newExecutor(1, new ThreadPoolExecutor.AbortPolicy(), 1000);

        // First task: occupies the only worker thread (running)
        executor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
        });

        // Second task: cannot start immediately, so it goes into the queue
        executor.execute(() -> {
        });


        // Third task: no thread available + queue full -> rejected -> throw reject exception
        Assertions.assertThrows(RejectedExecutionException.class,
                () -> executor.execute(() -> {
                }));

        // Verify rejectCount was incremented by our custom handler wrapper
        assertEquals(1, executor.getRejectCount().get());
    }

    @Test
    void testRejectHandlerToString() {
        // given
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
        executor = newExecutor(1, handler, 1000);

        // when
        RejectedExecutionHandler wrapped = ((ThreadPoolExecutor) executor).getRejectedExecutionHandler();
        String expected = handler.getClass().getSimpleName();
        String actual = wrapped.toString();

        // then
        assertEquals(expected, actual, "Wrapped handler.toString() should return original handler simple name");
    }

    @Test
    void testShutdownWithAwaitTerminationSuccess() {
        executor = newExecutor(1, new ThreadPoolExecutor.AbortPolicy(), 100);
        executor.execute(() -> {
        });
        executor.shutdown();
        Assertions.assertTrue(executor.isShutdown());
    }

    @Test
    void testShutdownWithTimeout() {
        executor = newExecutor(1, new ThreadPoolExecutor.AbortPolicy(), 10);

        // Submit long-running task so shutdown cannot complete in time
        executor.execute(() ->
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
            }
        });
        executor.shutdown();
        Assertions.assertTrue(executor.isShutdown());
    }

    @Test
    @SneakyThrows
    void testShutdownInterrupted() {
        executor = newExecutor(1, new ThreadPoolExecutor.AbortPolicy(), 50);

        // Start a thread that interrupts the main thread during shutdown
        Thread.currentThread().interrupt();
        executor.shutdown();

        // Clear interrupted flag for JVM stability
        Assertions.assertTrue(Thread.interrupted());
    }
}