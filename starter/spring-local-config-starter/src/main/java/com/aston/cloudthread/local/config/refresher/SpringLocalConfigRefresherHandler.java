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
package com.aston.cloudthread.local.config.refresher;

import com.aston.cloudthread.config.common.starter.refresher.AbstractCloudThreadPoolRefresher;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.spring.base.support.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Cloud Config refresher for dynamic thread pool.
 */
@Slf4j(topic = "CloudThreadLocalConfigRefresher")
public class SpringLocalConfigRefresherHandler extends AbstractCloudThreadPoolRefresher implements ApplicationListener<EnvironmentChangeEvent> {

    public SpringLocalConfigRefresherHandler(BootstrapConfigProperties props) {
        super(props);
    }

    @Override
    protected void registerListener() throws Exception {
        log.info("Spring Cloud Config refresher registered for cloud dynamic thread pool");
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Set<String> changedKeys = event.getKeys().stream()
                .filter(k -> k.startsWith(BootstrapConfigProperties.PREFIX))
                .collect(Collectors.toSet());

        if (changedKeys.isEmpty()) {
            log.info("No cloudthread configs modification detected, no need to refresh local" +
                    " thread pool configs");
            return;
        }

        ConfigurableEnvironment env =
                (ConfigurableEnvironment) ApplicationContextHolder.CONTEXT.getEnvironment();
        Map<String, Object> refreshProps = new HashMap<>();
        for (String key : changedKeys) {
            Object value = env.getProperty(key);
            if (value != null) {
                refreshProps.put(key, value);
            }
        }
        // call our new impl KV based properties local thread pool config refresher
        super.refreshThreadPoolProperties(refreshProps);
        log.info("CloudThread local thread pools refreshed based on updated config keys");
    }
}