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
package com.aston.cloudthread.spring.consul.config.starter.refresher;

import com.aston.cloudthread.config.common.starter.refresher.AbstractCloudThreadPoolRefresher;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * Consul config refresher for dynamic thread pool.
 * Listens for EnvironmentChangeEvent triggered by Spring Cloud Consul watch.
 */
@Slf4j(topic = "CloudThreadConsulRefresher")
public class SpringCloudConsulConfigRefresherHandler extends AbstractCloudThreadPoolRefresher {
    // todo: add spring cloud consul event handler here, and let it be the sub-class inner
    //  member variable

    public SpringCloudConsulConfigRefresherHandler(BootstrapConfigProperties props) {
        super(props);
    }

    @Override
    protected void registerListener() throws Exception {
        log.info("Consul refresher registered for cloud dynamic thread pool");
    }
}
