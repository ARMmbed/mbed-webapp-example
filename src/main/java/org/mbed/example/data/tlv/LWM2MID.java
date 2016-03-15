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

import java.util.Objects;

public class LWM2MID implements Comparable<LWM2MID> {
    
    private final String stringId;
    private int intId;
    
    public static LWM2MID from (int intId) {
        return new LWM2MID(intId);
    }
    
    public LWM2MID (int intId) {
        this.stringId = String.valueOf(intId);
        this.intId = intId;
    }
    
    public int intValue() {
        return intId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LWM2MID) {
            LWM2MID that = (LWM2MID) obj;
            return Objects.equals(this.stringId,that.stringId);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(intId);
    }
    
    @Override
    public String toString() {
        return stringId;
    }

    @Override
    public int compareTo(LWM2MID that) {
        if (this.stringId == null) {
            return that.stringId == null ? 0 : -1; 
        } else {
            return that.stringId == null ? 1 : this.stringId.compareTo(that.stringId);
        }
    }

}
