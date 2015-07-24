/*
 * Copyright (c) 2015, ARM Limited, All Rights Reserved
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbed.example.data;

/**
 * @author szymon
 */
public final class ResourceValue {
    private final String value;
    private final long timestamp;
    private final boolean isWaitingForResponse;
    private final int statusCode;
    private final String errorMessage;

    public ResourceValue(String value, boolean isWaitingForResponse, int statusCode, String errorMessage) {
        this(value, isWaitingForResponse, statusCode, errorMessage, System.currentTimeMillis());
    }

    public ResourceValue(String value, boolean isWaitingForResponse, int statusCode, String errorMessage, long timestamp) {
        this.value = value;
        this.isWaitingForResponse = isWaitingForResponse;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isWaitingForResponse() {
        return isWaitingForResponse;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceValue that = (ResourceValue) o;

        if (timestamp != that.timestamp) {
            return false;
        }
        if (isWaitingForResponse != that.isWaitingForResponse) {
            return false;
        }
        if (statusCode != that.statusCode) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        return !(errorMessage != null ? !errorMessage.equals(that.errorMessage) : that.errorMessage != null);

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (isWaitingForResponse ? 1 : 0);
        result = 31 * result + statusCode;
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result;
    }
}
