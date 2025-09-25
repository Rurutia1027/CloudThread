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
