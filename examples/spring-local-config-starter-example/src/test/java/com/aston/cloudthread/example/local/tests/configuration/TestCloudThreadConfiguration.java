package com.aston.cloudthread.example.local.tests.configuration;

import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.core.toolkit.ThreadPoolExecutorBuilder;
import com.aston.cloudthread.spring.base.CloudDynamicThreadPool;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadPoolExecutor;

@TestConfiguration
public class TestCloudThreadConfiguration {
    @Bean
    @CloudDynamicThreadPool
    public ThreadPoolExecutor withDynamicAnnotation() {
        return ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("cloudthread-with-dynamic-annotation")
                .corePoolSize(2)
                .maximumPoolSize(4)
                .keepAliveTimeSeconds(9999L)
                .awaitTerminationMillis(5000L)
                .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
                .threadFactory("cloudthread-with-dynamic-annotation_")
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .dynamicPool()
                .build();
    }

    @Bean
    public ThreadPoolExecutor withoutDynamicAnnotation() {
        return ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("cloudthread-without-dynamic-annotation")
                .corePoolSize(4)
                .maximumPoolSize(6)
                .keepAliveTimeSeconds(9999L)
                .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
                .threadFactory("cloudthread-without-dynamic-annotation_")
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .dynamicPool()
                .allowCoreThreadTimeOut(false)
                .build();
    }
}