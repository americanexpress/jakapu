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

package com.americanexpress.jakapu.controller;

import com.americanexpress.jakapu.service.Publisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class KafkaPublisherRestController {

    protected final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private Publisher publisher;

    /**
     * Set below headers in your REST http request
     * headerMap.put("source-uniqueid", "123456789");
     * headerMap.put("event-type", "TestEventType");
     * headerMap.put("source-type", "MySource1");
     * headerMap.put("source-timestamp", "1558389175962");
     *
     * @param payLoad
     * @param reqHeaders
     * @return
     */

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody String payLoad,
                                          @RequestHeader Map<String, String> reqHeaders) {

        ResponseEntity.BodyBuilder builder;
        Boolean bool = false;
        try {
            logger.info("request header = " + reqHeaders + " Payload = " + payLoad);

            bool = publisher.publish(reqHeaders, payLoad);

            if (bool)
                builder = ResponseEntity.ok();
            else
                builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            logger.error(e);
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return builder.body("Published to Kafka = " + bool.toString());
    }

}
