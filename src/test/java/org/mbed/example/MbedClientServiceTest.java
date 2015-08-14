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
import com.arm.mbed.restclient.entity.notification.EndpointDescription;
import com.arm.mbed.restclient.entity.notification.ResourceNotification;
import org.junit.Test;

/**
 * @author szymon
 */
public class MbedClientServiceTest {

    @Test
    public void testAddingAndRemoving() throws Exception {
        EndpointContainer epContainer = new EndpointContainer();
        MbedClientService.NotificationListenerImpl notificationListener = new MbedClientService.NotificationListenerImpl(epContainer);

        notificationListener.onEndpointsRegistered(new EndpointDescription[]{new EndpointDescription("endpoint1", null, null, null)});
        assertEquals(1, epContainer.getAllEndpoints().size());

        notificationListener.onEndpointsRegistered(new EndpointDescription[]{new EndpointDescription("endpoint2", null, null, null)});
        assertEquals(2, epContainer.getAllEndpoints().size());

        notificationListener.onEndpointsRegistered(new EndpointDescription[]{new EndpointDescription("endpoint1", null, null, null)});
        assertEquals(2, epContainer.getAllEndpoints().size());

        notificationListener.onEndpointsRemoved(new String[]{"endpoint1"});
        assertEquals(1, epContainer.getAllEndpoints().size());

        notificationListener.onEndpointsUpdated(new EndpointDescription[]{new EndpointDescription("endpoint2", null, null, null)});
        assertEquals(1, epContainer.getAllEndpoints().size());

        notificationListener.onEndpointsUpdated(new EndpointDescription[]{new EndpointDescription("notExisted", null, null, null)});
        assertEquals(2, epContainer.getAllEndpoints().size());

        notificationListener.onEndpointsExpired(new String[]{"endpoint2"});
        assertEquals(1, epContainer.getAllEndpoints().size());

        notificationListener.onEndpointsExpired(new String[]{"endpoint1"});
        assertEquals(1, epContainer.getAllEndpoints().size());

        notificationListener.onResourcesUpdated(new ResourceNotification[]{new ResourceNotification("endpoint1", null, "/dev/mac", null, new byte[]{2, 3, 4}, 0, 0)});
        assertEquals(1, epContainer.getAllEndpoints().size());
    }

}