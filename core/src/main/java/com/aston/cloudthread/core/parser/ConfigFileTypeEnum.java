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
package com.aston.cloudthread.core.parser;

import lombok.Getter;

/**
 * Enum representing supported configuration file types for CloudThread.
 *
 * <p>
 * This enum defines the file formats that can be used for configuration:
 * PROPERTIES, YML and YAML. It provides a utility method {@link #of(String)}
 * to map a string value to the corresponding enum, defaulting to PROPERTIES
 * if the input is unrecognized.
 * </p>
 */

@Getter
public enum ConfigFileTypeEnum {
    /**
     * Standard Java properties file
     */
    PROPERTIES("properties"),

    /**
     * YAML file with .yml extension
     */
    YML("yml"),

    /**
     * YAML fiel with .yaml extension
     */
    YAML("yaml");

    private final String value;

    ConfigFileTypeEnum(String value) {
        this.value = value;
    }

    public static ConfigFileTypeEnum of(String value) {
        for (ConfigFileTypeEnum typeEnum : ConfigFileTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return PROPERTIES;
    }
}
