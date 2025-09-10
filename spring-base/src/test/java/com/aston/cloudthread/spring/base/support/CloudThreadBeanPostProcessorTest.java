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
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.spring.base.configuration.CloudThreadBaseConfiguration;
import com.aston.cloudthread.spring.base.configuration.CloudThreadBaseTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = {CloudThreadBaseConfiguration.class, CloudThreadBaseTestConfig.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
class CloudThreadBeanPostProcessorTest {
    @Autowired
    private ApplicationContextHolder contextHolder;

    @Autowired
    private CloudThreadBeanPostProcessor postProcessor;

    @Autowired
    private BootstrapConfigProperties props;

    @BeforeEach
    void setup() {
        // clear registry cloud thread cache each setup
        CloudThreadRegistry.clear();
    }

    @Test
    void initOK() {
        assertNotNull(postProcessor);
        assertNotNull(props);
    }

}