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
package com.aston.cloudthread.core.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebThreadPoolConfigChangeDTO {
    /**
     * Web container name
     */
    private String webContainerName;

    /**
     * Web active profile
     */
    private String activeProfile;

    /**
     * Web container belong application name
     */
    private String applicationName;

    /**
     * Web container belong application UID/identifier
     */
    private String identify;

    /**
     * Web container thread pool config change notification receivers
     */
    private String receives;

    /**
     * Config option collection:
     * Key: config name (e.g., corePoolSize, maximumPoolSize, etc.)
     * Value: config value pair with
     * value1: config value before modify;  value2: config value after modify
     */
    private Map<String, ChangePair<?>> changes;

    /**
     * Thread pool config option update timestamp
     */
    private String updateTime;

    @Data
    @AllArgsConstructor
    public static class ChangePair<T> {
        private T before;
        private T after;
    }
}
