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
package com.aston.cloudthread.example.local.tests;

import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.example.local.LocalCloudThreadTestApp;
import com.aston.cloudthread.example.local.tests.configuration.TestCloudThreadConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest(
        classes = {LocalCloudThreadTestApp.class},
        properties = {
                "spring.config.import=optional:classpath:/application.yaml",
                "spring.profiles.active=test"
        }
)
@Import(TestCloudThreadConfiguration.class)
public class LocalConfigCloudThreadPoolTest {
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
        Assertions.assertNotNull(CloudThreadRegistry.getAllWrappers());
    }

    /**
     * - thread-pool-uid: cloudthread-with-dynamic-annotation
     * core-pool-size: 9
     * maximum-pool-size: 10
     * queue-capacity: 500
     * keep-alive-time-seconds: 30
     * working-queue: LinkedBlockingQueue
     * rejected-handler: AbortPolicy
     * allow-core-thread-timeout: true
     */
    @Test
    public void annotationEnableThreadPool_Inner_Params_Should_Be_Same_AS_YAML() {
        Assertions.assertEquals(cloudThreadPool1.getCorePoolSize(), 9);
        Assertions.assertEquals(cloudThreadPool1.getMaximumPoolSize(), 10);
        Assertions.assertTrue(cloudThreadPool1.allowsCoreThreadTimeOut());
        Assertions.assertEquals(cloudThreadPool1.getQueue().remainingCapacity(), 500);
        Assertions.assertTrue(cloudThreadPool1 instanceof CloudThreadExecutor);
        CloudThreadExecutor executor = (CloudThreadExecutor) cloudThreadPool1;
        Assertions.assertEquals(executor.getKeepAliveTime(TimeUnit.SECONDS), 30);
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
    public void annotationEnableThreadPool_Inner_Params_Should_NOT_Be_Same_AS_YAML() {
        Assertions.assertEquals(cloudThreadPool2.getCorePoolSize(), 4);
        Assertions.assertEquals(cloudThreadPool2.getMaximumPoolSize(), 6);
        Assertions.assertFalse(cloudThreadPool2.allowsCoreThreadTimeOut());
        Assertions.assertEquals(cloudThreadPool2.getQueue().remainingCapacity(), 0);
        Assertions.assertTrue(cloudThreadPool2 instanceof CloudThreadExecutor);
        CloudThreadExecutor executor = (CloudThreadExecutor) cloudThreadPool2;
        Assertions.assertEquals(executor.getKeepAliveTime(TimeUnit.SECONDS), 9999L);
    }
}
