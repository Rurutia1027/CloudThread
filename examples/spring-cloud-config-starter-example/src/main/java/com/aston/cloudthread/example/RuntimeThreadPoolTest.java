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
package com.aston.cloudthread.example;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RuntimeThreadPoolTest {
    private final ThreadPoolExecutor cloudThreadProducer;
    private final ThreadPoolExecutor cloudThreadConsumer;

    public RuntimeThreadPoolTest(
            @Qualifier("cloudThreadProducer") ThreadPoolExecutor cloudThreadProducer,
            @Qualifier("cloudThreadConsumer") ThreadPoolExecutor cloudThreadConsumer) {
        this.cloudThreadProducer = cloudThreadProducer;
        this.cloudThreadConsumer = cloudThreadConsumer;
    }

    private static final int MAX_TASK = Integer.MAX_VALUE;

    private final ExecutorService simulationExecutor = new ThreadPoolExecutor(
            2, 2,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactory() {
                private int count = 0;

                public Thread newThread(Runnable r) {
                    return new Thread(r, "simulator-thread-" + count++);
                }
            }
    );

    @PostConstruct
    public void test() {
        simulationExecutor.submit(() -> simulateHighActiveThreadUsage());

        simulationExecutor.submit(() -> simulateQueueUsageHigh());
    }

    @SneakyThrows
    private void simulateHighActiveThreadUsage() {
        for (int i = 0; i < MAX_TASK; i++) {
            TimeUnit.MILLISECONDS.sleep(10);
            try {
                cloudThreadProducer.execute(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(60); // mimic thread long time execution
                    } catch (InterruptedException e) {
                        System.out.println("Thread " + Thread.currentThread().getName() + " interrupted");
                        Thread.currentThread().interrupt();
                    } catch (Exception ignored) {
                        System.out.println("Thread " + Thread.currentThread().getName() + " Exception");
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }

    @SneakyThrows
    private void simulateQueueUsageHigh() {
        for (int i = 0; i < MAX_TASK; i++) {
            TimeUnit.MILLISECONDS.sleep(10);
            try {
                cloudThreadConsumer.execute(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(60); // 模拟长时间执行
                    } catch (InterruptedException e) {
                        System.out.println("Thread " + Thread.currentThread().getName() + " interrupted");
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }
}
