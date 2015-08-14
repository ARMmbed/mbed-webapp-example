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
package org.mbed.example;

import static org.junit.Assert.*;
import static org.mbed.example.resources.EndpointsResourceTest.*;
import com.arm.mbed.restclient.entity.notification.EndpointDescription;
import com.arm.mbed.restclient.entity.notification.ResourceInfo;
import com.arm.mbed.restclient.entity.notification.ResourceNotification;
import org.junit.Before;
import org.junit.Test;
import org.mbed.example.common.string.Utf8String;
import org.mbed.example.data.ResourcePath;
import org.mbed.example.data.ResourceValue;

/**
 * Created by mitvah01 on 15.7.2015.
 */
public class EndpointContainerTest {

    private EndpointContainer epContainer;

    @Before
    public void setUp() throws Exception {
        epContainer = new EndpointContainer();
    }

    @Test
    public void updateResource() throws Exception {
        epContainer.putEndpoints(new EndpointDescription[]{new EndpointDescription("dev-01", null, false, new ResourceInfo[]{new ResourceInfo("/temp", null, null, null, null)})});

        assertTrue(epContainer.updateResource(ResourcePath.of("dev-01", "/temp"), true));
        assertTrue(epContainer.getEndpointResourceValues("dev-01").get(ResourcePath.of("dev-01", "/temp")).isWaitingForResponse());

        //again same resource
        assertFalse(epContainer.updateResource(ResourcePath.of("dev-01", "/temp"), true));
        assertTrue(epContainer.getEndpointResourceValues("dev-01").get(ResourcePath.of("dev-01", "/temp")).isWaitingForResponse());

        epContainer.updateResource(ResourcePath.of("dev-01", "/temp"), mockEndpointResponse("25 C", 200));
        assertEquals(epContainer.getEndpointResourceValues("dev-01").get(ResourcePath.of("dev-01", "/temp")),
                new ResourceValue("25 C", false, 200, null, null, 0, false));

        epContainer.updateResource(ResourcePath.of("dev-01", "/temp"), "Some error");
        assertEquals(epContainer.getEndpointResourceValues("dev-01").get(ResourcePath.of("dev-01", "/temp")),
                new ResourceValue(null, false, 0, "Some error", null, 0, false));

        epContainer.updateResource(ResourcePath.of("dev-01", "/temp"), new ResourceNotification("endpoint1", null, null, null, new byte[]{2, 3, 4}, 0, 0));
        assertEquals(epContainer.getEndpointResourceValues("dev-01").get(ResourcePath.of("dev-01", "/temp")),
                new ResourceValue(Utf8String.from(new byte[]{2, 3, 4}), false, 200, null, null, 0, true));
    }
}