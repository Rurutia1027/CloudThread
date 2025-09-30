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
package com.aston.cloudthread.example.tests;

import com.aston.cloudthread.example.CloudThreadTestApp;
import com.aston.cloudthread.example.tests.config.TestCloudThreadConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ThreadPoolExecutor;


@Disabled("Skipping locally until Config Server is ready")
@SpringBootTest(
        classes = {CloudThreadTestApp.class},
        properties = {
                "spring.config.import=optional:classpath:/application.yaml",
                "spring.profiles.active=test"
        }
)
@Import(TestCloudThreadConfiguration.class)
public class CloudThreadPoolTest {
    @Autowired
    @Qualifier("withDynamicAnnotation")
    private ThreadPoolExecutor cloudThreadPool1;

    @Autowired
    @Qualifier("withoutDynamicAnnotation")
    private ThreadPoolExecutor cloudThreadPool2;

    @Test
    public void testInitOK() {
        Assertions.assertNotNull(cloudThreadPool1);
        Assertions.assertNotNull(cloudThreadPool2);
    }

    /**
     * executors:
     * - thread-pool-uid: cloudthread-with-dynamic-annotation
     * core-pool-size: 61
     * maximum-pool-size: 10
     * queue-capacity: 500
     * keep-alive-time-seconds: 30
     * working-queue: LinkedBlockingQueue
     * rejected-handler: AbortPolicy
     * allow-core-thread-timeout: true
     */
    @Test
    public void testCloudThreadPool1_Inner_Params_Should_Be_Override() {
        Assertions.assertEquals(cloudThreadPool1.getCorePoolSize(), 9);
    }

    /**
     * @Bean public ThreadPoolExecutor withoutDynamicAnnotation() {
     * return ThreadPoolExecutorBuilder.builder()
     * .threadPoolUID("cloudthread-without-dynamic-annotation")
     * .corePoolSize(4)
     * .maximumPoolSize(6)
     * .keepAliveTimeSeconds(9999L)
     * .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
     * .threadFactory("cloudthread-without-dynamic-annotation_")
     * .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
     * .dynamicPool()
     * .build();
     * }
     */
    @Test
    public void testCloudThreadPool2_Inner_Params_Should_NOT_Be_Override() {
        Assertions.assertEquals(cloudThreadPool2.getCorePoolSize(), 4);
    }
}
