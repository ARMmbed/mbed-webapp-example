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

import com.arm.mbed.restclient.MbedClient;
import com.arm.mbed.restclient.MbedClientBuilder;
import com.arm.mbed.restclient.MbedClientInitializationException;
import com.arm.mbed.restclient.NotificationListener;
import com.arm.mbed.restclient.entity.notification.EndpointDescription;
import com.arm.mbed.restclient.entity.notification.ResourceNotification;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;

/**
 * @author szymon
 */
@Singleton
@Path("mbed-client-service") //it's a DI hack
public class MbedClientService {

    private MbedClient client;
    private EndpointContainer endpointContainer;

    @Inject
    public MbedClientService() {
        this(null);
    }

    public MbedClientService(MbedClient mbedClient) {
        if (mbedClient == null) {
            //TODO: change this hardcoded connection
            createConnection("domain", "app2", "secret");
        } else {
            this.client = mbedClient;
            this.endpointContainer = new EndpointContainer();
        }
    }

    private void createConnection(String domain, String clientName, String clientSecret) {
        try {
            this.endpointContainer = new EndpointContainer();
            this.client = MbedClientBuilder.newBuilder().domain(domain).credentials(clientName, clientSecret)
                    .notifChannelHttpServer(0)
                    .notifListener(new NotificationListenerImpl(endpointContainer)).build(8080);

            readAllEndpoints();

        } catch (MbedClientInitializationException e) {
            e.printStackTrace();
        }
    }

    public MbedClient client() {
        return client;
    }

    public void readAllEndpoints() {
        endpointContainer.updateEndpointsList(client.endpoints().readAll());
    }

    public EndpointContainer endpointContainer() {
        return this.endpointContainer;
    }

    static class NotificationListenerImpl implements NotificationListener {
        private final EndpointContainer endpointContainer;

        NotificationListenerImpl(EndpointContainer endpointContainer) {
            this.endpointContainer = endpointContainer;
        }

        @Override
        public void onEndpointsRegistered(EndpointDescription[] endpoints) {
            endpointContainer.putEndpoints(endpoints);
        }

        @Override
        public void onEndpointsUpdated(EndpointDescription[] endpoints) {
            endpointContainer.putEndpoints(endpoints);
        }

        @Override
        public void onEndpointsRemoved(String[] endpointsRemoved) {
            endpointContainer.removeEndpoints(endpointsRemoved);
        }

        @Override
        public void onResourcesUpdated(ResourceNotification[] resourceNotifications) {
            //TODO: add notification handling
        }

        @Override
        public void onEndpointsExpired(String[] endpointsExpired) {
            endpointContainer.removeEndpoints(endpointsExpired);
        }
    }
}
