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

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.spring.base.CloudDynamicThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class CloudThreadBeanPostProcessor implements BeanPostProcessor {
    private final BootstrapConfigProperties properties;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CloudThreadExecutor) {
            CloudDynamicThreadPool cloudDynamicThreadPool;
            try {
                // Check whether the bean is annotated with @CloudDynamicThreadPool
                cloudDynamicThreadPool =
                        ApplicationContextHolder.findAnnotationOnBean(beanName,
                                CloudDynamicThreadPool.class);
                if (Objects.isNull(cloudDynamicThreadPool)) {
                    return bean;
                }
            } catch (Exception ex) {
                log.error("Failed to create cloud dynamic thread pool in annotation mode.",
                        ex);
                return bean;
            }

            CloudThreadExecutor cloudThreadExecutor = (CloudThreadExecutor) bean;

            // Load thread pool configuration from the config center
            // and apply it to the CloudThreadExecutor
            ThreadPoolExecutorProperties executorProperties = properties.getExecutors()
                    .stream()
                    .filter(item -> Objects.equals(cloudThreadExecutor.getThreadPoolUID(),
                            item.getThreadPoolUID()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("The Thread Pool UID does " +
                            "not exist in the configuration."));
            overrideLocalThreadPoolConfig(executorProperties, cloudThreadExecutor);

            // Register the CloudThreadExecutor into the CloudThreadRegistry.
            // ThreadPoolMonitor and alarms will later retrieve the thread pool instances
            // directly from the registry by its threadPoolUID
            CloudThreadRegistry.putWrapper(cloudThreadExecutor.getThreadPoolUID(),
                    cloudThreadExecutor, executorProperties);
        }
        return bean;
    }

    /**
     * Override the configuration of a local {@link CloudThreadExecutor} with values provided
     * from remote {@link ThreadPoolExecutorProperties}.
     *
     * <p>
     * The method:
     *     <ul>
     *         <li>Validates the corePoolSize does not exceed maximumPoolSize</li>
     *         <li>Adjusts pool sizes based on remote configuration.</li>
     *         <li>Replaces the work queue via reflection (since ThreadPoolExecutor has
     *         not setter)</li>
     *         <li>Updates keep-alive time, core-thread timeout setting, and rejection
     *         policy.</li>
     *     </ul>
     * </p>
     *
     * @param executorProperties  Remote thread pool configuration
     * @param cloudThreadExecutor Local thread pool instance to update
     */
    private void overrideLocalThreadPoolConfig(ThreadPoolExecutorProperties executorProperties,
                                               CloudThreadExecutor cloudThreadExecutor) {
        Integer remoteCorePoolSize = executorProperties.getCorePoolSize();
        Integer remoteMaximumPoolSize = executorProperties.getMaximumPoolSize();
        Assert.isTrue(remoteCorePoolSize <= remoteMaximumPoolSize, "[remoteCorePoolSize] must " +
                "be smaller than [remoteMaximumPoolSize]");

        int originalMaximumPoolSize = cloudThreadExecutor.getMaximumPoolSize();
        if (remoteCorePoolSize > originalMaximumPoolSize) {
            cloudThreadExecutor.setMaximumPoolSize(remoteMaximumPoolSize);
            cloudThreadExecutor.setCorePoolSize(remoteCorePoolSize);
        } else {
            cloudThreadExecutor.setCorePoolSize(remoteCorePoolSize);
            cloudThreadExecutor.setMaximumPoolSize(remoteMaximumPoolSize);
        }

        // The blocking queue does not provide a standard setter method, so we use reflection to set it.
        BlockingQueue workQueue =
                BlockingQueueTypeEnum.createBlockingQueue(executorProperties.getWorkingQueue(), executorProperties.getQueueCapacity());

        // In Java 9+ module system (JPMS), accessing private fields of JDK internal APIs via reflection is blocked by default.
        // Therefore, we need to explicitly open reflection permissions.
        // Add the following parameter in the startup command to open java.util.concurrent package:
        // IDE: add VM option: --add-opens=java.base/java.util.concurrent=ALL-UNNAMED
        // Deployment: in the startup script (e.g., java -jar), add:
        // java -jar --add-opens=java.base/java.util.concurrent=ALL-UNNAMED your-app.jar
        ReflectUtil.setFieldValue(cloudThreadExecutor, "workQueue", workQueue);
        cloudThreadExecutor.setKeepAliveTime(executorProperties.getKeeAliveTimeSeconds(), TimeUnit.SECONDS);
        cloudThreadExecutor.allowCoreThreadTimeOut(executorProperties.getAllowCoreThreadTimeout());
        cloudThreadExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
    }
}
