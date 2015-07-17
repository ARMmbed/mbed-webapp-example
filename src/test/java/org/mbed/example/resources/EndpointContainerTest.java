/*
 * Copyright 2015 ARM Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbed.example.resources;

import static org.junit.Assert.*;
import com.arm.mbed.restclient.entity.notification.EndpointDescription;
import org.junit.Test;

/**
 * Created by mitvah01 on 15.7.2015.
 */
public class EndpointContainerTest {
    @Test
    public void testAddingAndRemoving() throws Exception {
        EndpointContainer myNotificationListener = new EndpointContainer();
        myNotificationListener.onEndpointsRegistered(new EndpointDescription[]{new EndpointDescription("endpoint1", null, null, null)});
        assertEquals(1, myNotificationListener.getEndpointsList().size());
        myNotificationListener.onEndpointsRegistered(new EndpointDescription[]{new EndpointDescription("endpoint2", null, null, null)});
        assertEquals(2, myNotificationListener.getEndpointsList().size());
        myNotificationListener.onEndpointsRegistered(new EndpointDescription[]{new EndpointDescription("endpoint1", null, null, null)});
        assertEquals(2, myNotificationListener.getEndpointsList().size());
        myNotificationListener.onEndpointsRemoved(new String[]{"endpoint1"});
        assertEquals(1, myNotificationListener.getEndpointsList().size());
        myNotificationListener.onEndpointsUpdated(new EndpointDescription[]{new EndpointDescription("endpoint2", null, null, null)});
        assertEquals(1, myNotificationListener.getEndpointsList().size());
        myNotificationListener.onEndpointsUpdated(new EndpointDescription[]{new EndpointDescription("notExisted", null, null, null)});
        assertEquals(2, myNotificationListener.getEndpointsList().size());
        myNotificationListener.onEndpointsExpired(new String[]{"endpoint2"});
        assertEquals(1, myNotificationListener.getEndpointsList().size());
        myNotificationListener.onEndpointsExpired(new String[]{"endpoint1"});
        assertEquals(1, myNotificationListener.getEndpointsList().size());
    }
}