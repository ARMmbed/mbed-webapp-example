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
package org.mbed.example.resources;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by szymon
 */
public class PreSubscriptionEntry {

    @SerializedName("endpoint-name")
    private String endpointName;
    @SerializedName("endpoint-type")
    private String endpointType;
    @SerializedName("resource-path")
    private List<String> uriPathPatterns;

    public PreSubscriptionEntry() {

    }

    public PreSubscriptionEntry(String endpointName, String endpointType, List<String> uriPathPatterns) {
        this.endpointName = endpointName;
        this.endpointType = endpointType;
        this.uriPathPatterns = !uriPathPatterns.isEmpty() ? new ArrayList<>(uriPathPatterns) : null;
    }

    public List<String> getUriPathPatterns() {
        return uriPathPatterns;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public String getEndpointType() {
        return endpointType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PreSubscriptionEntry) {
            PreSubscriptionEntry that = (PreSubscriptionEntry) obj;
            return Objects.equals(this.endpointName, that.endpointName) &&
                    Objects.equals(this.endpointType, that.endpointType) &&
                    Objects.equals(this.uriPathPatterns, that.uriPathPatterns);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(endpointName + endpointType, uriPathPatterns);
    }

}
