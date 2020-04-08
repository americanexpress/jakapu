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

package com.americanexpress.jakapu.exceptions;

/**
 * Error Code Enum constants
 */
public enum ErrorCodes {
    JAKAPU_SUCCESS("PUBLISHED SUCCESSFULLY", "JAKAPU_1000"),
    JAKAPU_PARSING_FAILED("PARSING FAILED", "JAKAPU_1020"),

    JAKAPU_SERVICE_DOWN(" PUBLISHER SERVICE RUNTIME ERROR", "JAKAPU_1030"),
    JAKAPU_SERVICE_TIMEOUT(" SERVICE TIMED OUT", "JAKAPU_1031"),
    JAKAPU_INTERNAL_ERROR(" INTERNAL ERROR", "JAKAPU_1032"),


    JAKAPU_HEADER_EMPTY(" HEADER ATTRIBUTE(s) IS(ARE) EMPTY FOR  HEADER(S): ", "JAKAPU_1040"),
    JAKAPU_INAVALID_MESSAGE_TYPE(" HEADER ATTRIBUTE MESSAGE_TYPE VALUE IS NOT VALID", "JAKAPU_1041"),
    JAKAPU_TOPIC_HEADER_MAPPING_EMPTY(" NO MAPPING FOUND FOR THE HEADER", "JAKAPU_1042"),
    JAKAPU_HEADER_VALUE_ERROR(" HEADER ATTRIBUTE'S VALUE IS EMPTY OR NOT IN THE CORRECT FORMAT FOR HEADER(S): ", "JAKAPU_1043"),

    JAKAPU_PAYLOAD_EMPTY(" PAYLOAD EMPTY", "JAKAPU_1050"),

    DEFAULT("DEFAULT", "JAKAPU_0000");
    private String message;
    private String code;

    ErrorCodes(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }


    public String getDescription(String customMessage) {
        return "ErrorCode: " + code + " Description: " + message + " Message: " + customMessage;
    }
}
