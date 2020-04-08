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

package com.americanexpress.jakapu.constants;


import com.americanexpress.jakapu.exceptions.ErrorCodes;
import com.americanexpress.jakapu.exceptions.PublishingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum Validator {

    INSTANCE;

    /**
     * validate the kafka msg header vs headers config in yaml
     *
     * @param headerMap
     * @param mandatoryHeaders
     * @param references
     * @return
     */
    public boolean validateMessageHeader(Map<String, String> headerMap, Map<String, String> mandatoryHeaders, Object... references) {
        Set<String> mandatoryHeadersSet = mandatoryHeaders.keySet().stream().collect(Collectors.toSet());
        HashMap<String, String> headerTopicMap = (HashMap<String, String>) references[0];
        boolean isValid = true;
        StringBuilder erronousHeaders = new StringBuilder();

/** add those headerAttribute below which you want to behave as mandatory kafka headers which
 *   should be published from kafka publisher
 */
        isValid &= headerMap.keySet().containsAll(mandatoryHeadersSet);
        if (!isValid) {
            mandatoryHeadersSet.removeAll(headerMap.keySet());
            throw new PublishingException(ErrorCodes.JAKAPU_HEADER_EMPTY.getMessage() + mandatoryHeadersSet, ErrorCodes.JAKAPU_HEADER_EMPTY);
        }

/**
 * ## below topic names should be passed as value to "source-type" header
 * ## example Key Value =  source-type : MySource1
 */
        isValid &= mandatoryHeaders.entrySet().stream().map(entry -> {
            String value = entry.getValue();
            boolean result = true;
            if (value.startsWith("re(")) {
                String referenceName = value.substring(value.indexOf("re(") + 3, value.indexOf(")"));
                switch (referenceName) {
                    case "jakapu.psf.payload.headerTopicMap":
                        if (!headerTopicMap.keySet().contains(headerMap.get(entry.getKey()))) {
                            erronousHeaders.append(entry.getKey() + " |");
                            result &= false;
                        }
                        break;
                    default:
                        throw new PublishingException(ErrorCodes.JAKAPU_PARSING_FAILED.getMessage(), ErrorCodes.JAKAPU_PARSING_FAILED);
                }
            }
            if (value.startsWith("rx(")) {
                String regex = value.substring(value.indexOf("rx(") + 3, value.indexOf(")"));
                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(headerMap.get(entry.getKey()));

                if (!matcher.matches()) {
                    erronousHeaders.append(entry.getKey() + " |");
                    result &= false;
                }
            }
            return result;
        }).reduce((x1, x2) -> ((Boolean) x1 && (Boolean) x2)).orElse(false);

        if (!isValid) {
            throw new PublishingException(ErrorCodes.JAKAPU_HEADER_VALUE_ERROR.getMessage() + erronousHeaders.toString(), ErrorCodes.JAKAPU_HEADER_VALUE_ERROR);
        }

        return isValid;
    }

}
