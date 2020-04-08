/*
 * Copyright 2020 American Express Travel Related Services Company, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.americanexpress.jakapu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * spring config for topic header mapping
 */

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jakapu.psf.payload")
public class TopicMappingConfig {

    private HashMap<String, String> headerAttribute;

    private HashMap<String, String> headerTopicMap;

    public HashMap<String, String> getHeaderTopicMap() {
        return headerTopicMap;
    }

    public void setHeaderTopicMap(HashMap<String, String> headerTopicMap) {
        this.headerTopicMap = headerTopicMap;
    }

    public HashMap<String, String> getHeaderAttribute() {
        return headerAttribute;
    }

    public void setHeaderAttribute(HashMap<String, String> headerAttribute) {
        this.headerAttribute = headerAttribute;
    }
}
