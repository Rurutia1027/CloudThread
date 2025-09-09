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

import java.io.IOException;
import java.util.List;

/**
 * Interface for config parser
 */
public interface ConfigParser {
    /**
     * Whether current config parser support given type of file.
     *
     * @param type of config file {.properties | .yml | .yaml}
     * @return support parse or not
     */
    boolean supports(ConfigFileTypeEnum type);

    /**
     * Converted parsed content and organized them into k,v pairs.
     *
     * @param content config file content
     * @return parsed key,value pair in map
     * @throws IOException throw exception when parse failed
     */
    boolean doParse(String content) throws IOException;

    /**
     * Fetch current parser supports config file type list
     *
     * @return collection of enums of supported parsed types of config files.
     */
    List<ConfigFileTypeEnum> getConfigFileTypes();
}
