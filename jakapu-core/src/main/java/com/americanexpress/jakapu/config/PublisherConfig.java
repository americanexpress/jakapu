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

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * spring config for kafka infra settings
 */

@Component
@Configuration
public class PublisherConfig {


    protected final Logger logger = LogManager.getLogger(this.getClass());
    @Value("${jakapu.kafka.bootstrap-servers}")
    private List<String> bootStrapServers;

    @Value("${jakapu.kafka.security.protocol}")
    private String securityProtocol;

    @Value("${jakapu.kafka.security.enabled}")
    private String securitEnabled;

    @Value("${jakapu.kafka.ssl.protocol}")
    private String sslProtocol;

    @Value("${jakapu.kafka.ssl.keystore.type}")
    private String keystoreType;

    @Value("${jakapu.kafka.ssl.keystore.location}")
    private String keystoreLocation;

    @Value("${jakapu.kafka.ssl.keystore.password}")
    private String keystorePassword;

    @Value("${jakapu.kafka.ssl.key.password}")
    private String keyPassword;

    @Value("${jakapu.kafka.ssl.truststore.location}")
    private String truststoreLocation;

    @Value("${jakapu.kafka.ssl.truststore.password}")
    private String truststorePassword;


    @Bean
    public Map<String, Object> SenderConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, "1");

        logger.info("security protocol {}", securityProtocol);


        if (Boolean.valueOf(securitEnabled)) {
            props.put("security.protocol", securityProtocol);
            props.put("ssl.protocol", sslProtocol);
            props.put("ssl.keystore.type", keystoreType);
            props.put("ssl.keystore.location", keystoreLocation);
            props.put("ssl.keystore.password", keystorePassword);
            props.put("ssl.key.password", keyPassword);
            props.put("ssl.truststore.location", truststoreLocation);
            props.put("ssl.truststore.password", truststorePassword);
        }

        return props;

    }

    @Bean
    public ProducerFactory<String, String> senderFactory() {
        return new DefaultKafkaProducerFactory<>(SenderConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(senderFactory());
    }


}
