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
package com.aston.cloudthread.spring.cloud.config.starter.refresher;

import cn.hutool.core.util.StrUtil;
import com.aston.cloudthread.config.common.starter.refresher.AbstractCloudThreadPoolRefresher;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

/**
 * Spring Cloud Config refresher for dynamic thread pool.
 */
@Slf4j(topic = "CloudThreadCloudConfigRefresher")
public class SpringCloudConfigRefresherHandler extends AbstractCloudThreadPoolRefresher
        implements ApplicationListener<EnvironmentChangeEvent> {
    private final Environment environment;

    public SpringCloudConfigRefresherHandler(BootstrapConfigProperties props,
                                             Environment environment) {
        super(props);
        this.environment = environment;
    }

    @Override
    protected void registerListener() throws Exception {
        log.info("Spring Cloud Config refresher registered for cloud dynamic thread pool");
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        // filter only relevant keys for thread pool
        if (event.getKeys().stream().anyMatch(key -> key.startsWith(BootstrapConfigProperties.PREFIX))) {
            // read the full config value as string
            String configInfo = environment.getProperty(BootstrapConfigProperties.PREFIX);
            if (StrUtil.isNotBlank(configInfo)) {
                // delegate to parent method to parse, detect changes, and publish event
                refreshThreadPoolProperties(configInfo);
                log.info("Cloud thread pool properties refreshed via Spring Cloud Config");
            }
        }
    }
}
