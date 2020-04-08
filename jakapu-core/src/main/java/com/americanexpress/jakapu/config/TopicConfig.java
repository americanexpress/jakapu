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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * spring config for kafka topic settings
 */

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jakapu.psf.publisher")

public class TopicConfig {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    private List<TopicConfiguration> topics;

    public List<TopicConfiguration> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicConfiguration> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "Topic Configurations [topics=" + topics + "]";
    }

    //Mapping individual topic from YML file
    public static class TopicConfiguration {


        private String name;
        private String id;

        private Integer noPartitions;

        private Integer replicationFactor;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getNumPartitions() {
            return noPartitions;
        }

        public void setNumPartitions(Integer noPartitions) {
            this.noPartitions = noPartitions;
        }

        public Integer getReplicationFactor() {
            return replicationFactor;
        }

        public void setReplicationFactor(Integer replicationFactor) {
            this.replicationFactor = replicationFactor;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "TopicConfiguration [name=" + name + ", id=" + id + ", noPartitions=" + noPartitions
                    + ", replicationFactor=" + replicationFactor + "]";
        }


    }


}
