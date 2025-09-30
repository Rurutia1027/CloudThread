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
package com.aston.cloudthread.spring.base.support;

import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.spring.base.configuration.CloudThreadBaseConfiguration;
import com.aston.cloudthread.spring.base.configuration.CloudThreadBaseTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = {CloudThreadBaseConfiguration.class, CloudThreadBaseTestConfig.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
class CloudThreadBeanPostProcessorTest {
    @Autowired
    private CloudThreadBeanPostProcessor postProcessor;

    @Autowired
    private BootstrapConfigProperties props;

    @Autowired
    private CloudThreadExecutor dynamicExecutor;

    @BeforeEach
    void setup() {
        // clear registry cloud thread cache each setup
        CloudThreadRegistry.clear();
    }

    @Test
    void initOK() {
        assertNotNull(postProcessor);
        assertNotNull(props);
        assertNotNull(dynamicExecutor);
    }

    @Test
    void testPostProcessorBeanInjection() {
        assertNotNull(postProcessor, "CloudThreadBeanPostProcessor should be injected");
        assertNotNull(props, "BootstrapConfigProperties should be injected");
    }

    @Test
    void testNonCloudThreadExecutorBeanReturnsSameInstance() {
        Object plainBean = new Object();
        Object result = postProcessor.postProcessAfterInitialization(plainBean, "plainBean");
        assertSame(plainBean, result, "Non-CloudThreadExecutor beans should return unchanged");
    }

    @Test
    void testCloudThreadExecutorWithoutAnnotation() {
        CloudThreadExecutor executor = newExecutor(UUID.randomUUID().toString(),
                1, new ThreadPoolExecutor.CallerRunsPolicy(), 1000L);
        Object result = postProcessor.postProcessAfterInitialization(executor, "executorBean");

        // Should return same instance
        assertSame(executor, result);

        // Should not be registered in registry
        assertTrue(CloudThreadRegistry.getAllWrappers().isEmpty());
    }

    @Test
    void testDynamicOverride() {
        // Configure override for dynamicExecutor
        ThreadPoolExecutorProperties overrideProps = ThreadPoolExecutorProperties.builder()
                .threadPoolUID("dynamic-pool")
                .corePoolSize(5)
                .maximumPoolSize(10)
                .queueCapacity(20)
                .workingQueue(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE.getName())
                .allowCoreThreadTimeout(true)
                .keepAliveTimeSeconds(1000L)
                .build();
        props.setExecutors(Collections.singletonList(overrideProps));

        // process bean (simulate Spring post-processing)
        postProcessor.postProcessAfterInitialization(dynamicExecutor, "dynamicExecutor");

        // verify the executor configuration is overridden dynamically
        assertEquals(5, dynamicExecutor.getCorePoolSize());
        assertEquals(10, dynamicExecutor.getMaximumPoolSize());
        assertEquals(20, dynamicExecutor.getQueue().remainingCapacity() + dynamicExecutor.getQueue().size());
        assertNotNull(CloudThreadRegistry.getWrapper("dynamic-pool"));
    }

    @Test
    void testDynamicOverrideWithInvalidConfigThrows() {
        ThreadPoolExecutorProperties badConfig = new ThreadPoolExecutorProperties();
        badConfig.setThreadPoolUID("dynamic-pool");
        badConfig.setCorePoolSize(10); // > max pool size
        badConfig.setMaximumPoolSize(5);
        props.setExecutors(Collections.singletonList(badConfig));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> postProcessor.postProcessAfterInitialization(dynamicExecutor, "dynamicExecutor"));

        assertTrue(ex.getMessage().contains("must be smaller than"));
    }

    private CloudThreadExecutor newExecutor(String tUID, int queueCapacity,
                                            RejectedExecutionHandler handler,
                                            long awaitTerminationMillis) {
        return new CloudThreadExecutor(
                tUID,
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
}