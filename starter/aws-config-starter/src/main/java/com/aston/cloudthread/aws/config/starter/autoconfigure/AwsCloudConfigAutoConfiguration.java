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
package com.aston.cloudthread.aws.config.starter.autoconfigure;

import com.aston.cloudthread.aws.config.starter.awsconfig.AwsCloudConfigFetcher;
import com.aston.cloudthread.aws.config.starter.refresher.AwsCloudConfigRefresherHandler;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.spring.base.enable.MarkerConfiguration;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Configurable
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable",
        matchIfMissing = true, havingValue = "true")
public class AwsCloudConfigAutoConfiguration {
    @Bean
    public AwsCloudConfigFetcher awsCloudConfigFetcher() {
        // TODO: refine this later for occupy compile ok first
        return new AwsCloudConfigFetcher(null);
    }

    @Bean
    public AwsCloudConfigRefresherHandler awsCloudConfigRefresherHandler(AwsCloudConfigFetcher fetcher, BootstrapConfigProperties props) {
        return new AwsCloudConfigRefresherHandler(fetcher, props);
    }
}
