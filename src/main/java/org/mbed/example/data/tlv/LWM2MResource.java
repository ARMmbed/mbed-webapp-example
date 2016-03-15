/*
 * Copyright (c) 2016, ARM Limited, All Rights Reserved
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
package org.mbed.example.data.tlv;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.mbed.example.common.string.Utf8String;

public class LWM2MResource extends LWM2MResourceInstance {

    private List<LWM2MResourceInstance> instances;

    private LWM2MResource (LWM2MID id) {
        super (id);
    }

    public LWM2MResource (LWM2MID id, byte[] value) {
        super (id, value);
        this.validate();
    }

    public LWM2MResource (LWM2MID id, List<LWM2MResourceInstance> instances) {
        this (id);
        this.instances = new ArrayList<>(instances);
        this.validate();
    }

    public final boolean hasNestedInstances() {
        return instances != null && !instances.isEmpty();
    }

    public List<LWM2MResourceInstance> getNestedInstances() {
        return instances;
    }

    @Override
    public String toString() {
        if(hasNestedInstances()){
            return getNestedInstances().stream()
                    .map(i -> "\t/" + getId() + "/" + i.getId() + "\t\t\t" + Utf8String.from(i.getValue()))
                    .collect(Collectors.joining("\r\n"));
        }else{
            return "\t/" + getId() + "\t\t\t" + Utf8String.from(getValue());
        }
    }

    private void validate() throws IllegalArgumentException {
        if (getId().intValue() < 0 || getId().intValue() > 65535) {
            throw new IllegalArgumentException("Resource ID must be between 0 and 65535.");
        }
        if (hasValue() == hasNestedInstances()) {
            throw new IllegalArgumentException("Resource must be a value or nested resource instances.");
        }
    }
}
