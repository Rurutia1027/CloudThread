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
