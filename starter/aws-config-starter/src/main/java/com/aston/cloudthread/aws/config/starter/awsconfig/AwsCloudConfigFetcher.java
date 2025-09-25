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
