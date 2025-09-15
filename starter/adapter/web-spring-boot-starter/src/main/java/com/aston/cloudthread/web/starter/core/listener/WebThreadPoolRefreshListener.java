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
package com.aston.cloudthread.web.starter.core.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.aston.cloudthread.config.common.starter.refresher.CloudThreadPoolConfigUpdateEvent;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.notification.dto.WebThreadPoolConfigChangeDTO;
import com.aston.cloudthread.core.notification.service.NotifierDispatcher;
import com.aston.cloudthread.spring.base.support.ApplicationContextHolder;
import com.aston.cloudthread.web.starter.core.config.WebThreadPoolConfig;
import com.aston.cloudthread.web.starter.core.service.WebThreadPoolService;
import com.aston.cloudthread.web.starter.core.metric.WebThreadPoolMetrics;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Listener for web thread pool configuration update events from the config center.
 * <p>
 * It compares the current runtime metrics with the latest remote configuration,
 * applies updates when changes are detected, and dispatches notifications to subscribers.
 * </p>
 */
@RequiredArgsConstructor
public class WebThreadPoolRefreshListener implements ApplicationListener<CloudThreadPoolConfigUpdateEvent> {
    private final WebThreadPoolService webThreadPoolService;
    private final NotifierDispatcher notifierDispatcher;

    /**
     * Handle configuration update events.
     * <p>
     * If the incoming configuration differs from the current thread pool metrics,
     * update the thread pool and trigger a notification.
     *
     * @param event web thread pool config update event
     * </p>
     */
    @Override
    public void onApplicationEvent(CloudThreadPoolConfigUpdateEvent event) {
        BootstrapConfigProperties.WebThreadPoolExecutorConfig webExecutorConfig =
                event.getBootstrapConfigProperties().getWebConfig();
        if (Objects.isNull(webExecutorConfig)) {
            return;
        }

        WebThreadPoolMetrics basicMetrics = webThreadPoolService.getMetrics();
        if (!Objects.equals(basicMetrics.getCorePoolSize(), webExecutorConfig.getCorePoolSize())
                || !Objects.equals(basicMetrics.getMaximumPoolSize(), webExecutorConfig.getMaximumPoolSize())
                || !Objects.equals(basicMetrics.getKeepAliveTimeSeconds(),
                webExecutorConfig.getKeepAliveTimeSeconds())) {

            // Apply updated thread pool configuration
            webThreadPoolService.updateThreadPool(BeanUtil.toBean(webExecutorConfig, WebThreadPoolConfig.class));

            // Notify subscribers of the configuration change
            sendWebThreadPoolConfigChangeMessage(basicMetrics, webExecutorConfig);
        }
    }

    /**
     * Build and send a configuration change notification message.
     *
     * @param originalProps current runtime metrics before update
     * @param remoteProps   new configuration from the config center
     */
    @SneakyThrows
    private void sendWebThreadPoolConfigChangeMessage(WebThreadPoolMetrics originalProps,
                                                      BootstrapConfigProperties.WebThreadPoolExecutorConfig remoteProps) {
        Environment environment = ApplicationContextHolder.getBean(Environment.class);
        String activeProfile = environment.getProperty("spring.profiles.active", "dev");
        String applicationName = environment.getProperty("spring.application.name");

        Map<String, WebThreadPoolConfigChangeDTO.ChangePair<?>> changes = new HashMap<>();
        changes.put("corePoolSize", new WebThreadPoolConfigChangeDTO.ChangePair<>(originalProps.getCorePoolSize(), remoteProps.getCorePoolSize()));
        changes.put("maximumPoolSize", new WebThreadPoolConfigChangeDTO.ChangePair<>(originalProps.getMaximumPoolSize(), remoteProps.getMaximumPoolSize()));
        changes.put("keepAliveTime",
                new WebThreadPoolConfigChangeDTO.ChangePair<>(originalProps.getKeepAliveTimeSeconds(), remoteProps.getKeepAliveTimeSeconds()));

        WebThreadPoolConfigChangeDTO configChangeDTO = WebThreadPoolConfigChangeDTO.builder()
                .activeProfile(activeProfile)
                .identify(InetAddress.getLocalHost().getHostAddress())
                .applicationName(applicationName)
                .webContainerName(webThreadPoolService.getWebContainerType().getName())
                .receives(remoteProps.getNotifyConfig().getSubscribers())
                .changes(changes)
                .updateTime(DateUtil.now())
                .build();
        notifierDispatcher.sendWebChangeMessage(configChangeDTO);
    }
}
