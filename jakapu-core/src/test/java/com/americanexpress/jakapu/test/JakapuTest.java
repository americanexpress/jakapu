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

package com.americanexpress.jakapu.test;

import com.americanexpress.jakapu.config.TopicConfig;
import com.americanexpress.jakapu.config.TopicMappingConfig;
import com.americanexpress.jakapu.constants.Validator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.*;

@SpringBootTest()
public class JakapuTest {

    private static final String TEMPLATE_TOPIC = "TestKafkaTopic";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, TEMPLATE_TOPIC);

    KafkaTemplate<Integer, String> template;
    BlockingQueue<ConsumerRecord<Integer, String>> records;
    Map<String, Object> senderProps;
    KafkaMessageListenerContainer<Integer, String> container;

    @Before
    public void setUp() throws Exception {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testCG", "false",
                embeddedKafka.getEmbeddedKafka());

        DefaultKafkaConsumerFactory<Integer, String> cf =
                new DefaultKafkaConsumerFactory<Integer, String>(consumerProps);

        ContainerProperties containerProperties = new ContainerProperties(TEMPLATE_TOPIC);

        container =
                new KafkaMessageListenerContainer<>(cf, containerProperties);

        records = new LinkedBlockingQueue<>();

        container.setupMessageListener(new MessageListener<Integer, String>() {

            @Override
            public void onMessage(ConsumerRecord<Integer, String> record) {
                System.out.println(record);
                records.add(record);
            }

        });
        container.setBeanName("JakapuTests");
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());

        senderProps =
                KafkaTestUtils.senderProps(embeddedKafka.getEmbeddedKafka().getBrokersAsString());

        ProducerFactory<Integer, String> pf =
                new DefaultKafkaProducerFactory<Integer, String>(senderProps);
        template = new KafkaTemplate<>(pf);
        template.setDefaultTopic(TEMPLATE_TOPIC);
    }

    @Test
    public void testValidator() {

        TopicMappingConfig topicMappingConfig = new TopicMappingConfig();

        HashMap<String, String> headerAttribute = new HashMap<String, String>();
        headerAttribute.put("source-type", "re(jakapu.psf.payload.headerTopicMap)");
        headerAttribute.put("event-type", "rx(\\w{1,})");
        headerAttribute.put("source-uniqueid", "rx(\\w{1,})");
        headerAttribute.put("source-timestamp", "rx(\\d{13})");


        HashMap<String, String> headerTopicMap = new HashMap<String, String>();
        headerTopicMap.put("MySource1", "t1");

        topicMappingConfig.setHeaderAttribute(headerAttribute);
        topicMappingConfig.setHeaderTopicMap(headerTopicMap);


        TopicConfig topicConf = new TopicConfig();

        List<TopicConfig.TopicConfiguration> topics = new ArrayList<>();

        TopicConfig.TopicConfiguration topicConfiguration = new TopicConfig.TopicConfiguration();
        topicConfiguration.setId("t1");
        topicConfiguration.setName(TEMPLATE_TOPIC);
        topicConfiguration.setNumPartitions(1);
        topicConfiguration.setReplicationFactor(1);

        topics.add(topicConfiguration);

        topicConf.setTopics(topics);

        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("source-type", "MySource1");
        reqHeaders.put("event-type", "MyEventType");
        reqHeaders.put("source-uniqueid", "123456789");
        reqHeaders.put("source-timestamp", "1558389175100");

        assertEquals(Validator.INSTANCE.validateMessageHeader(reqHeaders, topicMappingConfig.getHeaderAttribute(), topicMappingConfig.getHeaderTopicMap()), true);

    }

    @Test
    public void testTemplate() throws Exception {

        template.sendDefault("foo");
        assertThat(records.poll(10, TimeUnit.SECONDS), hasValue("foo"));

        template.sendDefault(0, 2, "bar");
        ConsumerRecord<Integer, String> received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received, hasKey(2));
        assertThat(received, hasPartition(0));
        assertThat(received, hasValue("bar"));

        template.send(TEMPLATE_TOPIC, 0, 2, "baz");
        received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received, hasKey(2));
        assertThat(received, hasPartition(0));
        assertThat(received, hasValue("baz"));
    }

    @After
    public void tearDown() {
        // stop the container
        container.stop();
    }

}