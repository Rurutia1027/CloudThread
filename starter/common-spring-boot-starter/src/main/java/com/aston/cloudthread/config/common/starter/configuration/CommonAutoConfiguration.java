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
package com.aston.cloudthread.config.common.starter.configuration;

import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.spring.base.configuration.CloudThreadBaseConfiguration;
import com.aston.cloudthread.spring.base.enable.MarkerConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@ConditionalOnBean(MarkerConfiguration.Marker.class)
@Import(CloudThreadBaseConfiguration.class)
@AutoConfigureAfter(CloudThreadBaseConfiguration.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class CommonAutoConfiguration {
    @Bean
    public BootstrapConfigProperties bootstrapConfigProperties(Environment env) {
        BootstrapConfigProperties bootstrapConfigProperties = Binder.get(env)
                .bind(BootstrapConfigProperties.PREFIX,
                        Bindable.of(BootstrapConfigProperties.class))
                .get();
        BootstrapConfigProperties.setInstance(bootstrapConfigProperties);
        return bootstrapConfigProperties;
    }

    @Bean
    public CloudThreadBannerHandler cloudThreadBannerHandler(ObjectProvider<BuildProperties> buildProperties) {
        return new CloudThreadBannerHandler(buildProperties.getIfAvailable()); 
    }
}
