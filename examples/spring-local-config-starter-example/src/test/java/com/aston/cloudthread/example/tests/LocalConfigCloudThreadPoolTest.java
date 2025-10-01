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

import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.example.local.LocalCloudThreadTestApp;
import com.aston.cloudthread.example.configuration.TestCloudThreadConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Disabled("Skipping locally until Config Server is ready")
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
    private ThreadPoolExecutor dynamicPool;

    @Autowired
    @Qualifier("withoutDynamicAnnotation")
    private ThreadPoolExecutor staticPool;


    @Test
    public void testInitOK() {
        Assertions.assertNotNull(dynamicPool);
        Assertions.assertNotNull(staticPool);
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
        Assertions.assertEquals(dynamicPool.getCorePoolSize(), 9);
        Assertions.assertEquals(dynamicPool.getMaximumPoolSize(), 10);
        Assertions.assertTrue(dynamicPool.allowsCoreThreadTimeOut());
        Assertions.assertEquals(dynamicPool.getQueue().remainingCapacity(), 500);
        Assertions.assertTrue(dynamicPool instanceof CloudThreadExecutor);
        CloudThreadExecutor executor = (CloudThreadExecutor) dynamicPool;
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
        Assertions.assertEquals(staticPool.getCorePoolSize(), 4);
        Assertions.assertEquals(staticPool.getMaximumPoolSize(), 6);
        Assertions.assertFalse(staticPool.allowsCoreThreadTimeOut());
        Assertions.assertEquals(staticPool.getQueue().remainingCapacity(), 0);
        Assertions.assertTrue(staticPool instanceof CloudThreadExecutor);
        CloudThreadExecutor executor = (CloudThreadExecutor) staticPool;
        Assertions.assertEquals(executor.getKeepAliveTime(TimeUnit.SECONDS), 9999L);
    }
}
