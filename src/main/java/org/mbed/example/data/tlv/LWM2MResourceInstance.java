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


import org.mbed.example.common.string.HexArray;

public class LWM2MResourceInstance {

    private final LWM2MID id;
    private byte[] value;
    private LWM2MResourceType type;
    private LWM2MResourceType repType;

    protected LWM2MResourceInstance(LWM2MID id) {
        if (id != null) {
            this.id = id;
        } else {
            throw new NullPointerException("LWM2MID");
        }
    }

    public LWM2MResourceInstance(LWM2MID id, byte[] value) {
        this(id);
        this.value = value;

        if (value == null) {
            throw new IllegalArgumentException("Missing value from resource instance.");
        }
        this.repType = LWM2MResourceType.OPAQUE;
    }

    public LWM2MID getId() {
        return id;
    }

    public byte[] getValue() {
        return value;
    }

    public boolean hasValue() {
        return value != null;
    }

    @Override
    public String toString() {
        return "Resource instance [id:" + id + ", value: " + HexArray.toHex(value) + "]";
    }

}
