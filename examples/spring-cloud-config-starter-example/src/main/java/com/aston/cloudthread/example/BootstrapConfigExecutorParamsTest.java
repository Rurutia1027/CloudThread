///*
// * Copyright 2024 Rurutia1027
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * You may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// */
//package com.aston.cloudthread.example;
//
//import com.aston.cloudthread.core.config.BootstrapConfigProperties;
//import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Test class to validate the initialization of thread pool properties.
// *
// * <p>
// * All configuration values for the thread pools (cloudthread-producer and
// * cloudthread-consumer)
// * are loaded from the `application.yaml` file in the example module under the
// * `cloudthread.executors` section.
// * This ensures that BootstrapConfigProperties correctly binds YAML configuration into
// * Java objects.
// * </p>
// */
//@Slf4j
//@Component
//public class BootstrapConfigExecutorParamsTest {
//    // BootstrapConfigProperties instance already initialized via the AutoConfig
//    // Here we just import the already initialized instance to current context to test inner
//    // parameters match with the parameters defined in the application.yaml
//    @Autowired
//    private BootstrapConfigProperties bootstrapConfigProperties;
//
//    @PostConstruct
//    public void testBootstrapConfigPropInitOK() {
//        log.info("boot strap config instance cannot be null {}",
//                (bootstrapConfigProperties != null));
//    }
//
//    @PostConstruct
//    public void testBootStrapConfigPropInnerParamsInitOK() {
//        assertNotNull(bootstrapConfigProperties, "BootstrapConfigProperties should not be " +
//                "null!");
//        log.info("Bootstrap config instance loaded: {}", bootstrapConfigProperties != null);
//
//        List<ThreadPoolExecutorProperties> executors =
//                bootstrapConfigProperties.getExecutors();
//        assertNotNull(executors, "Executor properties list should not be null");
//        assertEquals(2, executors.size());
//
//        // Validate cloudthread-producer thread pool inner parameters
//        Optional<ThreadPoolExecutorProperties> producerOpt = executors.stream()
//                .filter(e -> "cloudthread-producer".equals(e.getThreadPoolUID()))
//                .findFirst();
//        assertTrue(producerOpt.isPresent(), "cloudthread-producer should exist");
//        ThreadPoolExecutorProperties producer = producerOpt.get();
//        assertEquals(5, producer.getCorePoolSize(), "Producer corePoolSize mismatch");
//        assertEquals(10, producer.getMaximumPoolSize(), "Producer maximumPoolSize mismatch");
//        assertEquals(500, producer.getQueueCapacity(), "Producer queueCapacity mismatch!");
//        assertEquals(30L, producer.getKeepAliveTimeSeconds(), "Producer keepAliveTimeSeconds" +
//                " mismatch!");
//        assertTrue(producer.getAllowCoreThreadTimeout(), "Producer allowCoreThreadTimeout " +
//                "should be true");
//
//        log.info("cloudthread-producer configuration is correct: {}", producer);
//
//        // Validate cloudthread-consumer
//        Optional<ThreadPoolExecutorProperties> consumerOpt = executors.stream()
//                .filter(e -> "cloudthread-consumer".equals(e.getThreadPoolUID()))
//                .findFirst();
//        assertTrue(consumerOpt.isPresent(), "cloudthread-consumer should exist");
//        ThreadPoolExecutorProperties consumer = consumerOpt.get();
//        assertEquals(10, consumer.getCorePoolSize(), "Consumer corePoolSize mismatch");
//        assertEquals(20, consumer.getMaximumPoolSize(), "Consumer maximumPoolSize mismatch");
//        assertEquals(1000, consumer.getQueueCapacity(), "Consumer queueCapacity mismatch");
//        assertEquals(60L, consumer.getKeepAliveTimeSeconds(), "Consumer keepAliveTimeSeconds mismatch");
//        assertTrue(consumer.getAllowCoreThreadTimeout(), "Consumer allowCoreThreadTimeout should be true");
//
//        log.info("cloudthread-consumer configuration is correct: {}", consumer);
//    }
//}
