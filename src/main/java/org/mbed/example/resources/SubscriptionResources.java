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

import com.arm.mbed.restclient.endpoint.PreSubscriptionEntry;
import com.arm.mbed.restclient.endpoint.PreSubscriptions;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitvah01 on 30.6.2015.
 */
@Path("/subscriptions")
@Singleton
public class SubscriptionResources {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationResource.class);
    private final MbedClientService clientCtr;

    @Inject
    SubscriptionResources(MbedClientService mbedClientService) {
        this.clientCtr = mbedClientService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PreSubscriptionEntry> getSubscriptions() {
        return clientCtr.client().preSubscriptions().read();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void putSubscription(List<PreSubscriptionEntry> preSubscriptionEntryList) {
        LOGGER.debug("Creating pre subscriptions.");
        PreSubscriptions.PreSubscriptionsBuilder preSubscriptionsBuilder = clientCtr.client().preSubscriptions().builder();

        if (!preSubscriptionEntryList.isEmpty()) {
            for (PreSubscriptionEntry preSubscriptionEntry : preSubscriptionEntryList) {
                preSubscriptionsBuilder.endpointName(preSubscriptionEntry.getEndpointName()).
                        endpointType(preSubscriptionEntry.getEndpointType());
                if (preSubscriptionEntry.getUriPathPatterns() != null) {
                    preSubscriptionsBuilder.path(preSubscriptionEntry.getUriPathPatterns().get(0));
                }
                preSubscriptionsBuilder.newEntry();
            }
        }
        preSubscriptionsBuilder.create();
    }
}
