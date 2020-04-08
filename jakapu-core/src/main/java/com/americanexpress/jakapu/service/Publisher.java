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


import com.americanexpress.jakapu.exceptions.PublishingException;

import java.util.Map;

public interface Publisher {


    /**
     * Implement this method to publish  a message to configured kafka topic.
     *
     * @param headerMap - request header map which needs to send to the kafka topic as record header
     * @param payLoad   - message payload or the message which needs to be send to the kafka topic
     * @return true - message published was successful
     * @throws PublishingException - In the implementation any known exception to the implementer should throw a PublishingException exception.
     */
    boolean publish(Map<String, String> headerMap, String payLoad) throws PublishingException;


    /**
     * Implement this method to publish  a message to configured kafka topic.
     *
     * @param headerMap - request header map which needs to send to the kafkatopic as record header
     * @param payLoad   - message payload or the message which needs to be send to the kafka topic
     * @return true - message published was successful
     * @throws PublishingException - In the implementation any known exception to the implementer should throw a PublishingException exception.
     */
    boolean publish(String messageKey, Map<String, String> headerMap, String payLoad) throws PublishingException;

}
