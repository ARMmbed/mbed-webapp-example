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

import com.arm.mbed.restclient.MbedClientBuilder;
import com.arm.mbed.restclient.MbedClientInitializationException;
import com.arm.mbed.restclient.endpoint.PreSubscriptionEntry;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.mbed.example.MbedClientService;

/**
 * Created by mitvah01 on 30.6.2015.
 */
@Path("/subscriptions")
@Singleton
public class SubscriptionResources {
    private final MbedClientService clientCtr;

    @Inject
    SubscriptionResources(MbedClientService clientCtr) {
        this.clientCtr = clientCtr;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PreSubscriptionEntry> getSubscriptions() {
        //client.preSubscriptions().builder().endpointType("Light").path("/3/0/1").path("/3/0/2").create();
        List<PreSubscriptionEntry> entries = null;
        entries = clientCtr.client().preSubscriptions().read();
        return entries;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public org.mbed.example.resources.PreSubscriptionEntry putSubscription(org.mbed.example.resources.PreSubscriptionEntry preSubscriptionEntry) {
        clientCtr.client().preSubscriptions().builder().endpointName(preSubscriptionEntry.getEndpointName()).
                endpointType(preSubscriptionEntry.getEndpointType());//.path(preSubscriptionEntry.getUriPathPatterns()).create();
        System.out.print("preSubscriptionEntry" + preSubscriptionEntry.getEndpointName() + ' ' + preSubscriptionEntry.getEndpointType());
        return preSubscriptionEntry;
    }
}
