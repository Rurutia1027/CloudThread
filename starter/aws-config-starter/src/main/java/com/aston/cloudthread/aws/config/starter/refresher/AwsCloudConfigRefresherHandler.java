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
package com.aston.cloudthread.aws.config.starter.refresher;

import com.aston.cloudthread.aws.config.starter.awsconfig.AwsCloudConfigFetcher;
import com.aston.cloudthread.config.common.starter.refresher.AbstractCloudThreadPoolRefresher;
import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * AWS Cloud Config refresher for dynamic thread pool.
 */
@Slf4j(topic = "CloudThreadCloudConfigRefresher")
public class AwsCloudConfigRefresherHandler extends AbstractCloudThreadPoolRefresher  {
    private final AwsCloudConfigFetcher fetcher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AwsCloudConfigRefresherHandler(AwsCloudConfigFetcher fetcher,
                                          BootstrapConfigProperties props) {
        super(props);
        this.fetcher = fetcher;
    }

    @Override
    protected void registerListener() throws Exception {
        log.info("Spring Cloud Config refresher registered for cloud dynamic thread pool");
    }
}
