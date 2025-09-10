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

import com.aston.cloudthread.core.alarm.ThreadPoolAlarmChecker;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.monitor.ThreadPoolMonitor;
import com.aston.cloudthread.core.notification.service.NotifierDispatcher;
import com.aston.cloudthread.spring.base.support.ApplicationContextHolder;
import com.aston.cloudthread.spring.base.support.CloudThreadBeanPostProcessor;
import com.aston.cloudthread.spring.base.support.SpringPropertiesLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * CloudThreadPool the Dynamic Thread Pool Spring Configuration class.
 */
@Configuration
public class CloudThreadBaseConfiguration {
    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @DependsOn("applicationContextHolder")
    public CloudThreadBeanPostProcessor cloudThreadBeanPostProcessor(BootstrapConfigProperties properties) {
        return new CloudThreadBeanPostProcessor(properties);
    }

@Bean
public NotifierDispatcher notifierDispatcher() {
    return new NotifierDispatcher();
}

    @Bean
    public SpringPropertiesLoader springPropertiesLoader() {
        return new SpringPropertiesLoader();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ThreadPoolAlarmChecker threadPoolAlarmChecker(NotifierDispatcher notifierDispatcher) {
        return new ThreadPoolAlarmChecker(notifierDispatcher);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ThreadPoolMonitor threadPoolMonitor() {
        return new ThreadPoolMonitor();
    }
}
