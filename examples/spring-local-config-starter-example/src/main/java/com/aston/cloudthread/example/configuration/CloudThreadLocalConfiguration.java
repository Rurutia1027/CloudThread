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
package com.aston.cloudthread.example.configuration;

import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.core.toolkit.ThreadPoolExecutorBuilder;
import com.aston.cloudthread.spring.base.CloudDynamicThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class CloudThreadLocalConfiguration {
    @Bean
    @CloudDynamicThreadPool
    public ThreadPoolExecutor cloudThreadProducer() {
        return ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("cloudthread-local-instance-1")
                .corePoolSize(2)
                .maximumPoolSize(4)
                .keepAliveTimeSeconds(9999L)
                .awaitTerminationMillis(5000L)
                .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
                .threadFactory("cloudthread-local-instance-1_")
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .dynamicPool()
                .build();
    }

    @Bean
    @CloudDynamicThreadPool
    public ThreadPoolExecutor cloudThreadConsumer() {
        return ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("cloudthread-local-instance-2")
                .corePoolSize(4)
                .maximumPoolSize(6)
                .keepAliveTimeSeconds(9999L)
                .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
                .threadFactory("cloudthread-local-instance-2_")
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .dynamicPool()
                .build();
    }
}
