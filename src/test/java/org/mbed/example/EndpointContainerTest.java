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
import org.mbed.example.data.tlv.TLV;

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

    @Test
    public void updateTlvResource() throws Exception {
        epContainer.putEndpoints(new EndpointDescription[]{new EndpointDescription("dev-02", null, false, new ResourceInfo[]{new ResourceInfo("/tlv", null, null, null, null)})});

        epContainer.updateResource(ResourcePath.of("dev-02", "/tlv"), mockEndpointResponse(createResourceTLV(), 200, TLV.CT_APPLICATION_LWM2M_TLV));
        assertEquals(epContainer.getEndpointResourceValues("dev-02").get(ResourcePath.of("dev-02", "/tlv")),
                new ResourceValue("\t/0\t\t\tARM\r\n\t/1\t\t\tmbed\r\n\t/6/0\t\t\t0x01\r\n\t/6/1\t\t\t0x14", false, 200, null, TLV.CT_APPLICATION_LWM2M_TLV, 0, false));

        epContainer.updateResource(ResourcePath.of("dev-02", "/tlv"), mockEndpointResponse(createObjectInstanceTLV(), 200, TLV.CT_APPLICATION_LWM2M_TLV));
        assertEquals(epContainer.getEndpointResourceValues("dev-02").get(ResourcePath.of("dev-02", "/tlv")),
                new ResourceValue("\t/0/0\t\t\t0x03\r\n" +
                                    "\t/0/2/1\t\t\t0xe0\r\n" +
                                    "\t/0/2/2\t\t\t0x80\r\n" +
                                    "\t/0/3\t\t\t0x01\r\n" +
                                    "\t/1/0\t\t\t0x04\r\n" +
                                    "\t/1/2/1\t\t\t0x80\r\n" +
                                    "\t/1/2/2\t\t\t0xe0\r\n" +
                                    "\t/1/3\t\t\t0x02", false, 200, null, TLV.CT_APPLICATION_LWM2M_TLV, 0, false));
    }

    private static byte[] createResourceTLV() {
        return new byte[]{
                (byte) 0b11_0_00_011,
                (byte) 0,
                'A', 'R', 'M',
                (byte) 0b11_0_01_000,
                (byte) 1,
                (byte) 4,
                'm', 'b', 'e', 'd',
                (byte) 0b10_0_00_110,
                (byte) 6,
                (byte) 0b01_0_00_001,
                (byte) 0,
                (byte) 0x01,
                (byte) 0b01_0_00_001,
                (byte) 1,
                (byte) 0x14,};
    }

    private static byte[] createObjectInstanceTLV(){
        //  /0/0        0x03
        //  /0/2/1      0xe0
        //  /0/2/2      0x80
        //  /0/3        0x01
        //  /1/0        0x04
        //  /1/2/1      0x80
        //  /1/2/2      0xe0
        //  /1/3        0x02
        return new byte[] {8,0,14,-63,0,3,-122,2,65,1,-32,65,2,-128,-63,3,1,8,1,14,-63,0,4,-122,2,65,1,-128,65,2,-32,-63,3,2};
    }
}