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

import cn.hutool.core.util.StrUtil;
import com.aston.cloudthread.config.common.starter.refresher.AbstractCloudThreadPoolRefresher;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

/**
 * Consul config refresher for dynamic thread pool.
 * Listens for EnvironmentChangeEvent triggered by Spring Cloud Consul watch.
 */
@Slf4j(topic = "CloudThreadConsulRefresher")
public class SpringCloudConsulConfigRefresherHandler extends AbstractCloudThreadPoolRefresher
        implements ApplicationListener<EnvironmentChangeEvent> {
    private final Environment environment;

    public SpringCloudConsulConfigRefresherHandler(BootstrapConfigProperties props, Environment environment) {
        super(props);
        this.environment = environment;
    }

    @Override
    protected void registerListener() throws Exception {
        log.info("Consul refresher registered for cloud dynamic thread pool");
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        try {
            if (event.getKeys().stream().anyMatch(k -> k.startsWith(BootstrapConfigProperties.PREFIX))) {
                String configInfo = environment.getProperty(BootstrapConfigProperties.PREFIX);
                if (StrUtil.isNotBlank(configInfo)) {
                    refreshThreadPoolProperties(configInfo);
                    log.info("Cloud thread pool properties refreshed via Consul");
                }
            }
        } catch (Exception e) {
            log.error("Error refreshing cloud thread pool from Consul", e);
        }
    }
}
