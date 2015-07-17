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

/**
 * Created by mitvah01 on 14.7.2015.
 */
public class Resources {
    private String uri;
    private String rt;
    private String ifDesc;
    private String type;
    private boolean obs;
    private String val;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public void setIfDesc(String ifDesc) {
        this.ifDesc = ifDesc;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setObs(boolean obs) {
        this.obs = obs;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getUri() {

        return uri;
    }

    public String getRt() {
        return rt;
    }

    public String getIfDesc() {
        return ifDesc;
    }

    public String getType() {
        return type;
    }

    public boolean isObs() {
        return obs;
    }

    public String getVal() {

        return val;
    }

    @Override
    public String toString() {
        return String.format("Resources [uri='%s', rt='%s', ifDesc='%s', type='%s', obs=%s, val='%s']", uri, rt, ifDesc, type, obs, val);
    }
}
