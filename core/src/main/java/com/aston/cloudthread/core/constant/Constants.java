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
package com.aston.cloudthread.core.constant;

public class Constants {

    /**
     * Log template for dynamic thread pool parameter changes
     */
    public static final String CHANGE_THREAD_POOL_TEXT = "[{}] Dynamic thread pool parameter changed:"
            + "\n    corePoolSize: {}"
            + "\n    maximumPoolSize: {}"
            + "\n    capacity: {}"
            + "\n    keepAliveTime: {}"
            + "\n    rejectedType: {}"
            + "\n    allowCoreThreadTimeOut: {}";

    /**
     * Separator constant for thread pool parameter changes (old => new)
     */
    public static final String CHANGE_DELIMITER = "%s => %s";

    /**
     * Slack message template for dynamic thread pool parameter change
     */
    public static final String SLACK_CONFIG_CHANGE_MESSAGE_TEXT = """
            *[Notice]* %s - Dynamic Thread Pool Parameter Change

            ---
            Thread Pool ID: %s
            Application Instance: %s
            Core Thread Count: %s
            Maximum Thread Count: %s
            Thread Keep Alive Time: %s
            Queue Type: %s
            Queue Capacity: %s
            Old Rejection Policy: %s
            New Rejection Policy: %s
            OWNER: @%s
            Note: Real-time notification for dynamic thread pool configuration changes (unlimited)

            ---
            *Change Time:* %s
            """;

    /**
     * Slack message template for web thread pool configuration change
     */
    public static final String SLACK_CONFIG_WEB_CHANGE_MESSAGE_TEXT = """
            *[Notice]* %s - %s Thread Pool Parameter Change

            ---
            Application Instance: %s
            Core Thread Count: %s
            Maximum Thread Count: %s
            Thread Keep Alive Time: %s
            OWNER: @%s
            Note: %s Thread Pool Configuration Change Real-time Notification (unlimited)

            ---
            *Change Time:* %s
            """;

    /**
     * Slack message template for dynamic thread pool alert
     */
    public static final String SLACK_ALARM_NOTIFY_MESSAGE_TEXT = """
            *[Alert]* %s - Dynamic Thread Pool Running Alert

            ---
            Thread Pool ID: %s
            Application Instance: %s
            Alert Type: %s

            ---
            Core Thread Count: %d
            Maximum Thread Count: %d
            Current Thread Count: %d
            Active Thread Count: %d
            Largest Concurrent Thread Count: %d
            Total Thread Pool Tasks: %d

            ---
            Queue Type: %s
            Queue Capacity: %d
            Queue Elements Count: %d
            Queue Remaining Capacity: %d

            ---
            Rejection Policy: %s
            Rejection Policy Execution Count: %d
            OWNER: @%s
            Note: This thread pool will not trigger duplicate alerts within %d minutes (configurable)

            ---
            *Alert Time:* %s
            """;
}