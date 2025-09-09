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

import com.aston.cloudthread.core.config.BootstrapConfigProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ConfigParserHandler is a centralized handler for parsing configuration content
 * into a normalized Map representation based on the configuration file type.
 *
 * <p>
 * It maintains a list of supported parsers (e.g., YAML and Properties parsers)
 * and delegates parsing to the appropriate parser based on the specified
 * {@link ConfigFileTypeEnum}.
 * </p>
 *
 * <p>
 * This class follows the singleton pattern, providing a globally accessible
 * instance via {@link #getInstance()}.
 * </p>
 *
 * <p>
 * Typical usage includes:
 * <ul>
 *     <li>Parsing raw configuration content from different sources.</li>
 *     <li>Refreshing application properties dynamically, e.g., thread pool settings.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Example:
 * <pre>
 * Map<Object, Object> configMap = ConfigParserHandler.getInstance()
 *     .parseConfig(rawYamlContent, ConfigFileTypeEnum.YAML);
 * </pre>
 * </p>
 *
 * <p>
 * Integration with Spring's Binder allows for dynamic binding of parsed
 * configurations to {@link BootstrapConfigProperties}, followed by publishing
 * a {@link ThreadPoolConfigUpdateEvent} to notify relevant listeners of configuration changes.
 * </p>
 */
public class ConfigParserHandler {
    private static final List<ConfigParser> PARSERS = new ArrayList<>();

    private ConfigParserHandler() {
        PARSERS.add(new YamlConfigParser());
        PARSERS.add(new PropsConfigParser());
    }

    public Map<Object, Object> parseConfig(String content, ConfigFileTypeEnum type) throws IOException {
        if (type == null || PARSERS.isEmpty()) {
            return Collections.emptyMap();
        }

        for (ConfigParser parser : PARSERS) {
            if (parser != null && parser.supports(type)) {
                if (content == null) {
                    return Collections.emptyMap();
                }
                return parser.doParse(content);
            }
        }
        return Collections.emptyMap();
    }

    public static ConfigParserHandler getInstance() {
        return ConfigParserHandlerHolder.INSTANCE;
    }

    private static class ConfigParserHandlerHolder {

        private static final ConfigParserHandler INSTANCE = new ConfigParserHandler();
    }
}
