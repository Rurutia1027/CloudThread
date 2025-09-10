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

import com.aston.cloudthread.core.notification.dto.ThreadPoolAlarmNotifyDTO;
import com.aston.cloudthread.core.notification.dto.ThreadPoolConfigChangeDTO;
import com.aston.cloudthread.core.notification.dto.WebThreadPoolConfigChangeDTO;

/**
 * NotifierService
 *
 * <p>Notification interface for sending thread pool configuration change events
 * and runtime alarm alerts.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *     <li>Send notifications when thread pool configurations change</li>
 *     <li>Send notifications for web thread pool configuration changes</li>
 *     <li>Send runtime alarm alerts for thread pools</li>
 * </ul>
 *
 * <p>Extensibility:</p>
 * <p>Custom implementations can integrate with different platforms (e.g., DingTalk,
 * WeCom, Slack, Email) while keeping the upper layers decoupled from the specifics
 * of each platform.</p>
 */
public interface NotifierService {
    /**
     * Send thread pool config options modify notify message.
     *
     * @param configChange thread pool config options change entity
     */
    void sendChangeMessage(ThreadPoolConfigChangeDTO configChange);

    /**
     * Send web thread pool config options modify notify message.
     *
     * @param configChange web's inner thread pool config options change entity
     */
    void sendWebChangeMessage(WebThreadPoolConfigChangeDTO configChange);

    /**
     * Send thread pool alarm notify message.
     *
     * @param alarm alarm message entity
     */
    void sendAlarmMessage(ThreadPoolAlarmNotifyDTO alarm);
}