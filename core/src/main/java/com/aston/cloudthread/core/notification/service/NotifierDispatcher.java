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
package com.aston.cloudthread.core.notification.service;

import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.notification.dto.ThreadPoolAlarmNotifyDTO;
import com.aston.cloudthread.core.notification.dto.ThreadPoolConfigChangeDTO;
import com.aston.cloudthread.core.notification.dto.WebThreadPoolConfigChangeDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * NotifierDispatcher
 *
 * <p>A unified notification dispatcher that hides the implementation details
 * of different notification platforms (e.g., DingTalk, Slack, WeCom, Email),
 * and provides a consistent {@link NotifierService} entry point for upper layers.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *     <li>Initialize various notifier service implementations using a simple factory pattern
 *         and keep them in a local cache</li>
 *     <li>Dynamically select the target notification platform based on configuration,
 *         achieving decoupling and flexible extension</li>
 *     <li>Add rate limiting to alarm messages to avoid duplicate or excessive notifications</li>
 * </ul>
 *
 * <p>Extensibility:</p>
 * <p>To add a new notification channel, simply implement {@link NotifierService}
 * and register it in the static initializer block.</p>
 *
 * <p>Thread-safety:</p>
 * <p>Different platform implementations are stored in an immutable
 * {@code Map<String, NotifierService>}, and dispatch logic uses {@link Optional}
 * to ensure safe invocations.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * NotifierDispatcher dispatcher = new NotifierDispatcher();
 * dispatcher.sendAlarmMessage(alarmDTO);
 * }</pre>
 */
public class NotifierDispatcher implements NotifierService {

    private static final Map<String, NotifierService> NOTIFIER_SERVICE_MAP = new HashMap<>();

    static {
        NOTIFIER_SERVICE_MAP.put("SLACK", new SlackMessageService());
        // EMAIL, WECHAT, DING, DISCORD, TEAMS
    }

    @Override
    public void sendChangeMessage(ThreadPoolConfigChangeDTO configChange) {
        getNotifierService().ifPresent(service -> service.sendChangeMessage(configChange));
    }

    @Override
    public void sendWebChangeMessage(WebThreadPoolConfigChangeDTO configChange) {
        getNotifierService().ifPresent(service -> service.sendWebChangeMessage(configChange));
    }

    @Override
    public void sendAlarmMessage(ThreadPoolAlarmNotifyDTO alarm) {
        getNotifierService().ifPresent(service -> {
            // Alarm frequency check
            boolean allowSend = AlarmRateLimiter.allowAlarm(
                    alarm.getThreadPoolUID(),
                    alarm.getAlarmType(),
                    alarm.getIntervalMinutes()
            );

            if (allowSend) {
                service.sendAlarmMessage(alarm.resolve());
            }
        });
    }

    private Optional<NotifierService> getNotifierService() {
        return Optional.ofNullable(BootstrapConfigProperties.getInstance().getNotifyPlatforms())
                .map(BootstrapConfigProperties.NotifyPlatformsConfig::getPlatform)
                .map(platform -> NOTIFIER_SERVICE_MAP.get(platform));
    }
}
