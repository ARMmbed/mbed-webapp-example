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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.arm.mbed.restclient.MbedClient;
import com.arm.mbed.restclient.endpoint.PreSubscriptionEntry;
import com.arm.mbed.restclient.endpoint.PreSubscriptions;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mbed.example.MbedClientService;

/**
 * Created by mitvah01 on 13.8.2015.
 */
public class SubscriptionResourceTest {
    private MbedClient mbedClient;
    private SubscriptionResources rest;
    private MbedClientService mbedClientService;

    @Before
    public void setUp() throws Exception {
        mbedClient = mock(MbedClient.class, RETURNS_DEEP_STUBS);

        mbedClientService = new MbedClientService(mbedClient);
        rest = new SubscriptionResources(mbedClientService);
    }

    @Test
    public void getSubscriptions() {
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry("dev-01", null, Arrays.asList("/s/*")),
                new PreSubscriptionEntry("dev-01", null, Arrays.asList("power*"))));
        assertEquals(2, rest.getSubscriptions().size());
    }

    @Test
    public void putSubscription() {
        PreSubscriptions.PreSubscriptionsBuilder preSubscriptionsBuilder = mock(PreSubscriptions.PreSubscriptionsBuilder.class, RETURNS_DEEP_STUBS);
        when(mbedClient.preSubscriptions().builder()).thenReturn(preSubscriptionsBuilder);
        rest.putSubscription(Arrays.asList(new PreSubscriptionEntry("dev-01", "light", Arrays.asList("/s/*"))));

        verify(preSubscriptionsBuilder.endpointName(eq("dev-01"))).endpointType(eq("light"));
        verify(preSubscriptionsBuilder).path(eq("/s/*"));
        verify(preSubscriptionsBuilder).create();
    }
}
