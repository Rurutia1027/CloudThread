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

import cn.hutool.http.HttpUtil;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.notification.dto.ThreadPoolAlarmNotifyDTO;
import com.aston.cloudthread.core.notification.dto.ThreadPoolConfigChangeDTO;
import com.aston.cloudthread.core.notification.dto.WebThreadPoolConfigChangeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Slack notification service via Slack Incoming Webhook:
 * POST - https://hooks.slack.com/services/
 */
@Slf4j
public class SlackMessageService implements NotifierService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public void sendChangeMessage(ThreadPoolConfigChangeDTO configChangeDTO) {
        Map<String, ThreadPoolConfigChangeDTO.ChangePair<?>> changes = configChangeDTO.getChanges();
        String text = String.format(
                "*[Thread Pool Configuration Change]*\nEnvironment: %s\nThread Pool ID: %s\nApplication: %s\nCore Threads: %s → %s\nMaximum Threads: %s → %s\nKeep-Alive Time: %s → %s\nQueue: %s\nQueue Capacity: %s → %s\nRejection Policy: %s → %s\nRecipients: %s\nUpdate Time: %s",
                configChangeDTO.getActiveProfile().toUpperCase(),
                configChangeDTO.getThreadPoolId(),
                configChangeDTO.getIdentify() + ":" + configChangeDTO.getApplicationName(),
                changes.get("corePoolSize").getBefore(), changes.get("corePoolSize").getAfter(),
                changes.get("maximumPoolSize").getBefore(), changes.get("maximumPoolSize").getAfter(),
                changes.get("keepAliveTime").getBefore(), changes.get("keepAliveTime").getAfter(),
                configChangeDTO.getWorkQueue(),
                changes.get("queueCapacity").getBefore(), changes.get("queueCapacity").getAfter(),
                changes.get("rejectedHandler").getBefore(),
                changes.get("rejectedHandler").getAfter(),
                configChangeDTO.getSubscribers(),
                configChangeDTO.getUpdateTime()
        );

        sendSlackMessage("Thread Pool Configuration Change Notification", text);
    }

    @Override
    public void sendWebChangeMessage(WebThreadPoolConfigChangeDTO configChangeDTO) {
        Map<String, WebThreadPoolConfigChangeDTO.ChangePair<?>> changes = configChangeDTO.getChanges();
        String text = String.format(
                "*[Web Container Thread Pool Configuration Change]*\nEnvironment: %s\nWeb Container: %s\nApplication: %s\nCore Threads: %s → %s\nMaximum Threads: %s → %s\nKeep-Alive Time: %s → %s\nRecipients: %s\nUpdate Time: %s",
                configChangeDTO.getActiveProfile().toUpperCase(),
                configChangeDTO.getWebContainerName(),
                configChangeDTO.getIdentify() + ":" + configChangeDTO.getApplicationName(),
                changes.get("corePoolSize").getBefore(), changes.get("corePoolSize").getAfter(),
                changes.get("maximumPoolSize").getBefore(), changes.get("maximumPoolSize").getAfter(),
                changes.get("keepAliveTime").getBefore(), changes.get("keepAliveTime").getAfter(),
                configChangeDTO.getReceives(),
                configChangeDTO.getUpdateTime()
        );

        sendSlackMessage(configChangeDTO.getWebContainerName() + " Thread Pool Notification", text);
    }


    @Override
    public void sendAlarmMessage(ThreadPoolAlarmNotifyDTO alarm) {
        String text = String.format(
                "*[Thread Pool Alarm]*\nEnvironment: %s\nThread Pool ID: %s\nApplication: %s\nAlarm Type: %s\nCore Threads: %s\nMaximum Threads: %s\nCurrent Threads: %s\nActive Threads: %s\nLargest Threads: %s\nCompleted Task Count: %s\nQueue: %s\nQueue Capacity: %s\nCurrent Queue Size: %s\nRemaining Capacity: %s\nRejection Policy: %s\nRejection Count: %s\nRecipients: %s\nInterval: %s\nTime: %s",
                alarm.getActiveProfile().toUpperCase(),
                alarm.getThreadPoolUID(),
                alarm.getIdentify() + ":" + alarm.getApplicationName(),
                alarm.getAlarmType(),
                alarm.getCorePoolSize(),
                alarm.getMaximumPoolSize(),
                alarm.getCurrentPoolSize(),
                alarm.getActivePoolSize(),
                alarm.getLargestPoolSize(),
                alarm.getCompletedTaskCount(),
                alarm.getWorkQueueName(),
                alarm.getWorkQueueCapacity(),
                alarm.getWorkQueueSize(),
                alarm.getWorkQueueRemainingCapacity(),
                alarm.getRejectedHandlerName(),
                alarm.getRejectCount(),
                alarm.getSubscribers(),
                alarm.getIntervalMinutes(),
                alarm.getCurrentTime()
        );

        sendSlackMessage("Thread Pool Alarm Notification", text);
    }


    /**
     * Generic Slack Webhook sending logic
     */
    private void sendSlackMessage(String title, String text) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", "*" + title + "*\n" + text);

        try {
            String serverUrl = BootstrapConfigProperties.getInstance().getNotifyPlatforms().getUrl();
            String requestBody = OBJECT_MAPPER.writeValueAsString(payload);

            String responseBody = HttpUtil.post(serverUrl, requestBody);
            log.info("Slack response: {}", responseBody);
        } catch (Exception ex) {
            log.error("Slack failed to send message.", ex);
        }
    }

    @Data
    static class SlackResponse {
        private String ok;
        private String error;
    }
}