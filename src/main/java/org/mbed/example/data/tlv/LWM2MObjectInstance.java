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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.mbed.example.common.string.Utf8String;

public class LWM2MObjectInstance {

    private final LWM2MID id;
    private final List<LWM2MResource> resources;

    public LWM2MObjectInstance (LWM2MID id, List<LWM2MResource> resources) {
        if (id != null) {
            this.id = id;
            this.resources = new ArrayList<>(resources);
        } else {
            throw new NullPointerException("LWM2MID");
        }
    }

    @Override
    public String toString() {
        return resources.stream()
                .map(r -> {
                    if(r.hasNestedInstances()){
                        return r.getNestedInstances().stream()
                                .map(i -> "\t/" + id + "/" + r.getId() + "/" + i.getId() + "\t\t\t" + Utf8String.from(i.getValue()))
                                .collect(Collectors.joining("\r\n"));
                    }else{
                        return "\t/" + id + "/" + r.getId() + "\t\t\t" + Utf8String.from(r.getValue());
                    }
                })
                .collect(Collectors.joining("\r\n"));
    }

}
