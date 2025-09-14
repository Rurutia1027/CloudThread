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

import cn.hutool.core.lang.ansi.AnsiColor;
import cn.hutool.core.lang.ansi.AnsiStyle;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.info.BuildProperties;

@Slf4j
public class CloudThreadBannerHandler implements InitializingBean {
    private static final String DYNAMIC_THREAD_POOL = " :: CloudThread :: ";
    private static final int STRAP_LINE_SIZE = 50;
    private final String version;

    public CloudThreadBannerHandler(BuildProperties buildProperties) {
        this.version = buildProperties != null ? buildProperties.getVersion() : "";
    }

    @Override
    public void afterPropertiesSet() {
        String banner = """
                 ,-----.,--.                 ,--. ,--------.,--.                              ,--. ,------.             ,--. 
                '  .--./|  | ,---.,--.,--. ,-|  | '--.  .--'|  ,---. ,--.--. ,---.  ,--,--. ,-|  | |  .--. ',---. ,---. |  | 
                |  |    |  || .-. |  ||  |' .-. |    |  |   |  .-.  ||  .--'| .-. :' ,-.  |' .-. | |  '--' | .-. | .-. ||  | 
                '  '--'\\|  |' '-' '  ''  '\\ `-' |    |  |   |  | |  ||  |   \\   --.\\ '-'  |\\ `-' | |  | --'' '-' ' '-' '|  | 
                 `-----'`--' `---' `----'  `---'     `--'   `--' `--'`--'    `----' `--`--' `---'  `--'     `---' `---' `--'  
                """;

        String bannerVersion = StrUtil.isNotEmpty(version) ? " (v" + version + ")" : "no version.";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (bannerVersion.length() + DYNAMIC_THREAD_POOL.length())) {
            padding.append(" ");
        }

        System.out.println(AnsiOutput.toString(
                banner,
                AnsiColor.CYAN, DYNAMIC_THREAD_POOL, AnsiColor.DEFAULT,
                padding.toString(), AnsiStyle.FAINT, bannerVersion,
                "\n"
        ));
    }
}
