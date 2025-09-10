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
package com.aston.cloudthread.spring.base.support;

import com.aston.cloudthread.core.config.ApplicationProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * SpringPropertiesLoader is a Spring-managed component responsible for
 * loading key Spring application properties at startup and populating the
 * global ApplicationProperties holder.
 * <p>
 * Specifically, it retrieves:
 * - spring.application.name: the name of the application (default:UNKNOWN)
 * - spring.profiles.active the currently active Spring profile (default:UNKNOWN)
 * <p>
 * Implements InitializingBean to ensure properties are set after dependency injection is
 * completed.
 */
public class SpringPropertiesLoader implements InitializingBean {
    @Value("${spring.application.name:UNKNOWN}")
    private String applicationName;

    @Value("${spring.profiles.active:UNKNOWN}")
    private String activeProfile;

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationProperties.setApplicationName(applicationName);
        ApplicationProperties.setActiveProfile(activeProfile);
    }
}
