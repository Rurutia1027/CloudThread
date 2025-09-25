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
package com.aston.cloudthread.aws.config.starter.awsconfig;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.util.Optional;

public class AwsCloudConfigFetcher {
    private final SsmClient ssmClient;

    public AwsCloudConfigFetcher(Region region) {
        this.ssmClient = SsmClient.builder().region(region).build();
    }

    /**
     * Fetch configuration value from AWS SSM Parameter Store
     */
    public Optional<String> fetchParameter(String parameterName) {
        try {
            GetParameterRequest request = GetParameterRequest.builder()
                    .name(parameterName)
                    .withDecryption(false) // 不加密
                    .build();
            GetParameterResponse response = ssmClient.getParameter(request);
            return Optional.ofNullable(response.parameter().value());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
