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
package com.aston.cloudthread.spring.base.configuration;

import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.spring.base.support.CloudThreadBeanPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest(classes = {CloudThreadBaseConfiguration.class, CloudThreadBaseTestConfig.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
class CloudThreadBaseConfigurationTest {
    @Autowired
    private CloudThreadBeanPostProcessor postProcessor;

    @Autowired
    private BootstrapConfigProperties properties;

    @Test
    void testPostProcessorWithEmptyConfig() {
        // simulate a bean that would normally be processed
        CloudThreadExecutor executor = newExecutor(UUID.randomUUID().toString(), 1,
                new ThreadPoolExecutor.AbortPolicy(), 1000L);
        Object result = postProcessor.postProcessAfterInitialization(executor, "dummyExecutor");

        assertSame(executor, result);
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