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

import com.arm.mbed.restclient.entity.notification.ResourceInfo;

/**
 * Created by mitvah01 on 14.7.2015.
 */
public final class ResourceMetadata {
    private final String uri;
    private final String rt;
    private final String ifDesc;
    private final String type;
    private final boolean obs;
    private final boolean isSubscribed;

    public ResourceMetadata(String uri, String rt, String ifDesc, String type, boolean obs, boolean isSubscribed) {
        this.uri = uri;
        this.rt = rt;
        this.ifDesc = ifDesc;
        this.type = type;
        this.obs = obs;
        this.isSubscribed = isSubscribed;
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

    public boolean isSubscribed() {
        return isSubscribed;
    }

    @Override
    public String toString() {
        return String.format("Resources [uri='%s', rt='%s', ifDesc='%s', type='%s', obs=%s]", uri, rt, ifDesc, type, obs);
    }

    public static ResourceMetadata from(ResourceInfo resourceInfo, boolean isSubscribed) {
        return new ResourceMetadata(resourceInfo.getPath(), resourceInfo.getRt(), resourceInfo.getInterfaceDescription(),
                resourceInfo.getCt(), resourceInfo.isObs(), isSubscribed);
    }

    public String getUriPath() {
        return uri;
    }
}
