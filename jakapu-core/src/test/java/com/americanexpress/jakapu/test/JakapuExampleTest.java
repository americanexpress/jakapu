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

import com.americanexpress.jakapu.annotations.EnableJakapu;
import com.americanexpress.jakapu.exceptions.ErrorCodes;
import com.americanexpress.jakapu.service.Publisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

/**
 * Example app to showcase how to convert an existing springboot app into
 * a kafka publisher just by adding @EnableJakapu annotation
 * this will also enable a rest end point to publish msgs to kafka
 * use it like below:
 * curl -H 'content-type: application/json' \
 * -H 'source-type: MySource1' \
 * -H 'event-type: TestEventType' \
 * -H 'source-uniqueid: 231324' \
 * -H 'source-timestamp: 1576020324880' \
 * --data '{"message":"test"}' \
 * https://hostname/jakapu/publish
 */

@SpringBootApplication
@ComponentScan(value = "com.americanexpress.jakapu")
@EnableJakapu
public class JakapuExampleTest implements CommandLineRunner {

    protected static final Logger logger = LogManager.getLogger(JakapuExampleTest.class);

    @Autowired
    private Publisher publisher;

    public static void main(String[] args) throws Exception {
        logger.info("Spring Boot JakapuExampleTest Started.............");

        SpringApplication.run(JakapuExampleTest.class, args);
    }

    /**
     * Sample method to show how to publish into kafka by invoking
     * publisher.publish()
     */
    public void publish() {

        Map<String, String> headerMap = new HashMap<String, String>();

        // add mandatory headers as per yaml config
        headerMap.put("source-uniqueid", "123456789");
        headerMap.put("event-type", "TestEventType");
        headerMap.put("source-type", "MySource1");
        headerMap.put("source-timestamp", "1558389175962");

        try {
            boolean bool = publisher.publish(headerMap, "Hello Jakapu ExampleTest");
            logger.info("status of JakapuExampleTest Kafka Publish - " + bool);

        } catch (Exception e) {
            logger.error(ErrorCodes.JAKAPU_INTERNAL_ERROR.getMessage() + ":" + ErrorCodes.JAKAPU_INTERNAL_ERROR.getCode(), e);
        }

    }

    @Override
    public void run(String... args) {
        this.publish();

    }


}

