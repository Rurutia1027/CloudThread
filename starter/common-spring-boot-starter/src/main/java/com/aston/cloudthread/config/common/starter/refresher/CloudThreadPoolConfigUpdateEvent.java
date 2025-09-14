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
package com.aston.cloudthread.config.common.starter.refresher;

import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

public class CloudThreadPoolConfigUpdateEvent extends ApplicationEvent {
    @Getter
    @Setter
    private BootstrapConfigProperties bootstrapConfigProperties;

    public CloudThreadPoolConfigUpdateEvent(Object source, BootstrapConfigProperties bootstrapConfigProperties) {
        super(source);
        this.bootstrapConfigProperties = bootstrapConfigProperties;
    }
}
