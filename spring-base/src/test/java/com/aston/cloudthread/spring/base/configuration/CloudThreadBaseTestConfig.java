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
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.core.monitor.ThreadPoolMonitor;
import com.aston.cloudthread.core.notification.service.NotifierDispatcher;
import com.aston.cloudthread.spring.base.CloudDynamicThreadPool;
import com.aston.cloudthread.spring.base.support.ApplicationContextHolder;
import com.aston.cloudthread.spring.base.support.CloudThreadBeanPostProcessor;
import com.aston.cloudthread.spring.base.support.SpringPropertiesLoader;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@TestConfiguration
public class CloudThreadBaseTestConfig {
    /**
     * Provide a test-scoped ApplicationContextHolder
     */
    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    /**
     * Provide a dummy BootstrapConfigProperties for testing
     */
    @Bean
    public BootstrapConfigProperties bootstrapConfigProperties() {
        BootstrapConfigProperties props = new BootstrapConfigProperties();
        ThreadPoolExecutorProperties executorProps = new ThreadPoolExecutorProperties();
        executorProps.setThreadPoolUID("dynamic-pool");
        executorProps.setCorePoolSize(2);
        executorProps.setMaximumPoolSize(4);
        executorProps.setWorkingQueue(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE.getName());
        executorProps.setQueueCapacity(100);
        executorProps.setKeeAliveTimeSeconds(60L);
        executorProps.setAllowCoreThreadTimeout(false);
        executorProps.setRejectedHandler("ABORT_POLICY");

        props.setExecutors(Collections.singletonList(executorProps));
        return props;
    }

    /**
     * Post-processor bean for dynamic thread pools
     */
    @Bean
    @DependsOn("applicationContextHolder")
    public CloudThreadBeanPostProcessor cloudThreadBeanPostProcessor(BootstrapConfigProperties props) {
        return new CloudThreadBeanPostProcessor(props);
    }

    /**
     * Notification dispatcher (dummy implementation can be injected)
     */
    @Bean
    public NotifierDispatcher notifierDispatcher() {
        return new NotifierDispatcher();
    }

    /**
     * Load Spring application properties
     */
    @Bean
    public SpringPropertiesLoader springPropertiesLoader() {
        return new SpringPropertiesLoader();
    }

    /**
     * Thread pool alarm checker, test-safe start/stop
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public ThreadPoolAlarmChecker threadPoolAlarmChecker(NotifierDispatcher notifierDispatcher) {
        return new ThreadPoolAlarmChecker(notifierDispatcher);
    }

    /**
     * Thread pool monitor, test-safe start/stop
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public ThreadPoolMonitor threadPoolMonitor() {
        return new ThreadPoolMonitor();
    }

    @Bean
    @CloudDynamicThreadPool
    public CloudThreadExecutor dynamicExecutor() {
        return new CloudThreadExecutor(
                "dynamic-pool",
                2, 4, 60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy(),
                1000
        );
    }
}
