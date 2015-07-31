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
    private final String contentType;
    private final int maxAge;

    public ResourceValue(String value, boolean isWaitingForResponse, int statusCode, String errorMessage, String contentType, int maxAge) {
        this(value, isWaitingForResponse, statusCode, errorMessage, System.currentTimeMillis(), contentType, maxAge);
    }

    public ResourceValue(String value, boolean isWaitingForResponse, int statusCode, String errorMessage, long timestamp, String contentType, int maxAge) {
        this.value = value;
        this.isWaitingForResponse = isWaitingForResponse;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
        this.contentType = contentType;
        this.maxAge = maxAge;
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

    public int getMaxAge() {
        return maxAge;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return String.format("ResourceValue [value='%s', isWaitingForResponse=%s, statusCode=%d, errorMessage='%s', contentType='%s', maxAge=%d, timestamp=%d]",
                value, isWaitingForResponse, statusCode, errorMessage, contentType, maxAge, timestamp);
    }

    @Override
    @SuppressWarnings("PMD.NPathComplexity")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceValue that = (ResourceValue) o;

        if (isWaitingForResponse != that.isWaitingForResponse) {
            return false;
        }
        if (statusCode != that.statusCode) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        if (maxAge != that.maxAge) {
            return false;
        }
        if (errorMessage != null ? !errorMessage.equals(that.errorMessage) : that.errorMessage != null) {
            return false;
        }
        return !(contentType != null ? !contentType.equals(that.contentType) : that.contentType != null);
    }

    @Override
    @SuppressWarnings("PMD.NPathComplexity")
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (isWaitingForResponse ? 1 : 0);
        result = 31 * result + statusCode;
        result = 31 * result + maxAge;
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result;
    }
}
