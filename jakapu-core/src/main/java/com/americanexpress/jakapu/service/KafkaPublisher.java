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

package com.americanexpress.jakapu.service;


import com.americanexpress.jakapu.config.PublisherConfig;
import com.americanexpress.jakapu.config.TopicConfig;
import com.americanexpress.jakapu.config.TopicMappingConfig;
import com.americanexpress.jakapu.constants.ApplicationConstants;
import com.americanexpress.jakapu.constants.Validator;
import com.americanexpress.jakapu.exceptions.ErrorCodes;
import com.americanexpress.jakapu.exceptions.PublishingException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class KafkaPublisher implements Publisher {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    private TopicConfig topicConfig;
    private PublisherConfig publisherConf;
    private TopicMappingConfig topicMapping;


    @Autowired
    public void setTopicConfig(TopicConfig topicConfig) {
        this.topicConfig = topicConfig;
    }

    @Autowired
    public void setSenderConf(PublisherConfig publisherConf) {
        this.publisherConf = publisherConf;
    }

    @Autowired
    public void setTopicMappingConfig(TopicMappingConfig topicMapping) {
        this.topicMapping = topicMapping;
    }


    /***
     * This method is used for publishing message to kafka topic
     *
     * @param headerMap  -- will go to kafka header
     * @param payLoad -- will go as kafka body
     * @return
     *
     */
    @Override
    public boolean publish(Map<String, String> headerMap, String payLoad) throws PublishingException {

        if (PublishMessagetoKafkaTopic(headerMap, payLoad, null))
            return true;

        return false;
    }

    /***
     * This method is used for publishing message to kafka topic
     * Message key will be the combination of Sourcetype-eventtype
     * @param messageKey
     * @param headerMap
     * @param payLoad
     *
     * @return
     *
     */
    @Override
    public boolean publish(String messageKey, Map<String, String> headerMap, String payLoad) throws PublishingException {
        if (PublishMessagetoKafkaTopic(headerMap, payLoad, messageKey))
            return true;

        return false;
    }

    /***
     * This method is used for publishing message to kafka topic
     *
     * @param messageKey
     * @param headerMap
     * @param payLoad
     * @return
     *
     */
    private boolean PublishMessagetoKafkaTopic(Map<String, String> headerMap, String payLoad, String messageKey) {
        String sourceType = headerMap.get(ApplicationConstants.header_key);
        logger.info("Source type is :{}", sourceType);

        // validate if all the mandatory header attribute as per yaml are in kafka header
        if (!Validator.INSTANCE.validateMessageHeader(headerMap, topicMapping.getHeaderAttribute(), topicMapping.getHeaderTopicMap())) {
            return false;
        }

        // get the mapping topic from the yml file for the particular source type
        String topicid = topicMapping.getHeaderTopicMap().get(sourceType);
        if (topicid == null) {

            throw new PublishingException(ErrorCodes.JAKAPU_TOPIC_HEADER_MAPPING_EMPTY.getMessage(),
                    ErrorCodes.JAKAPU_TOPIC_HEADER_MAPPING_EMPTY);
        }

        try {
            // get the topic details from the yml
            TopicConfig.TopicConfiguration topic = topicConfig.getTopics().stream().filter(u -> u.getId().equals(topicid)).findFirst()
                    .orElse(null);
            if (topic != null) {
                ProducerRecord<String, String> record = null;
                if (messageKey != null) {
                    record = new ProducerRecord<String, String>(messageKey, sourceType,
                            payLoad);
                } else {
                    record = new ProducerRecord<String, String>(topic.getName(), sourceType,
                            payLoad);
                }
                Headers headers = record.headers();
                headerMap.entrySet().stream().forEach(entry -> {
                    headers.add(new RecordHeader(entry.getKey(), entry.getValue().getBytes()));
                });

                logger.debug("Payload to the topic is :{} ", payLoad);

                publisherConf.kafkaTemplate().send(record);
            }
        } catch (Exception ex) {

            throw new PublishingException(ErrorCodes.JAKAPU_INTERNAL_ERROR.getMessage(), ex,
                    ErrorCodes.JAKAPU_INTERNAL_ERROR);

        }
        return true;
    }


}
