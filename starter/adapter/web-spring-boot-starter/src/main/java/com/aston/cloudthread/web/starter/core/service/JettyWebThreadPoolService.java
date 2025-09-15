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
package com.aston.cloudthread.web.starter.core.service;

import com.aston.cloudthread.web.starter.core.config.WebThreadPoolConfig;
import com.aston.cloudthread.web.starter.core.enums.WebContainerEnum;
import com.aston.cloudthread.web.starter.core.metric.WebThreadPoolMetrics;
import com.aston.cloudthread.web.starter.core.snapshot.WebThreadPoolSnapshot;
import org.springframework.boot.web.server.WebServer;

import java.util.concurrent.Executor;

public class JettyWebThreadPoolService extends AbstractWebThreadPoolService {
    @Override
    protected Executor getWebServerInnerExecutor(WebServer webServer) {
        return null;
    }

    @Override
    public void updateThreadPool(WebThreadPoolConfig config) {

    }

    @Override
    public WebThreadPoolMetrics getMetrics() {
        return null;
    }

    @Override
    public WebThreadPoolSnapshot getRuntimeSnapshot() {
        return null;
    }

    @Override
    public WebContainerEnum getWebContainerType() {
        return null;
    }
}
