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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread Pool Alarm Rate Limiter
 * <p>
 * This class provides a simple rate-limiting mechanism for thread pool alarms.
 * It ensures that repeated alarms of the same type for a specific thread pool
 * are suppressed if they occur within a configured interval.
 */
public class AlarmRateLimiter {
    /**
     * Alarm record cache. Key format: threadPoolId + "|" + alarmType
     */
    private static final Map<String, Long> ALARM_RECORD = new ConcurrentHashMap<>();

    /**
     * Checks whether an alarm is allowed to be sent based on the configured interval.
     *
     * @param threadPoolId    Thread pool ID
     * @param alarmType       Type of the alarm
     * @param intervalMinutes Minimum interval between alarms in minutes
     * @return true if sending is allowed; false if it should be suppressed
     */
    public static boolean allowAlarm(String threadPoolId, String alarmType, int intervalMinutes) {
        String key = buildKey(threadPoolId, alarmType);
        long currentTime = System.currentTimeMillis();

        return ALARM_RECORD.compute(key, (k, lastTime) -> {
            if (lastTime == null || (currentTime - lastTime) > intervalMinutes * 60 * 1000L) {
                return currentTime; // Update to current time
            }
            return lastTime; // Keep original time
        }) == currentTime; // Equal to current time means sending is allowed
    }

    private static String buildKey(String threadPoolId, String alarmType) {
        return threadPoolId + "|" + alarmType;
    }
}
