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
package com.aston.cloudthread.example.tests.benchmark;

import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.core.toolkit.ThreadPoolExecutorBuilder;
import com.aston.cloudthread.example.local.LocalCloudThreadTestApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Disabled("Reflect not allowed in CI pipeline, disable test cases")
@SpringBootTest(
        classes = LocalCloudThreadTestApp.class,
        properties = {
                "spring.config.import=optional:classpath:/application.yaml",
                "spring.profiles.active=test"
        }
)
public class ThreadPoolBenchmarkTest {

    @Autowired
    @Qualifier("withDynamicAnnotation")
    private ThreadPoolExecutor dynamicPool;

    private ThreadFactory threadFactory;

    @BeforeEach
    void setup() {
        threadFactory = Executors.defaultThreadFactory();
    }


    private void runBenchmark(String label, ThreadPoolExecutor executor) throws InterruptedException {
        long start = System.nanoTime();

        IntStream.range(0, 10_000).forEach(i ->
                executor.submit(() -> Math.log(Math.random() * 1000))
        );

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        long elapsed = (System.nanoTime() - start) / 1_000_000;
        System.out.println(label + " benchmark took: " + elapsed + " ms");
    }

    @Test
    void benchmarkStaticVsDynamic() throws Exception {
        // Static (code-defined, baseline)
        ThreadPoolExecutor staticPool = ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("benchmark-static")
                .corePoolSize(9)
                .maximumPoolSize(50)
                .keepAliveTimeSeconds(60)
                .workQueueType(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE)
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .threadFactory(threadFactory)
                .workQueueCapacity(2000)
                .build();

        runBenchmark("Static benchmark", staticPool);

        // Dynamic (loaded from application.yaml)
        runBenchmark("Dynamic benchmark (yaml)", dynamicPool);
    }
}