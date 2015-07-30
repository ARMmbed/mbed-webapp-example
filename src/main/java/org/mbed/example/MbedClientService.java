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
import com.arm.mbed.restclient.servlet.HttpServletChannel;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import org.mbed.example.data.ServerConfiguration;

/**
 * @author szymon
 */
@Singleton
@Path("mbed-client-service") //it's a DI hack
public class MbedClientService {

    private MbedClient client;
    private boolean connected = false;
    private EndpointContainer endpointContainer;

    @Inject
    public MbedClientService() {
        this(null);
    }

    public MbedClientService(MbedClient mbedClient) {
        if (mbedClient == null) {
            //TODO: change this hardcoded connection
            createConnection("http://localhost:8080", "domain/app2", "secret");
        } else {
            this.client = mbedClient;
            this.endpointContainer = new EndpointContainer();
            this.connected = true;
        }
    }

    public final void createConnection(ServerConfiguration configuration) {
        createConnection(configuration.getAddress(), configuration.getUsername(), configuration.getPassword());
    }
    
    public final void createConnection(String address, String clientName, String clientSecret) {
        connected = false;
        try {
            this.endpointContainer = new EndpointContainer();
            HttpServletChannel httpServletChannel = new HttpServletChannel(30, 2000);
            
            String[] clientCreds = clientName.split("/");
            if (clientCreds.length != 2) {
                throw new IllegalArgumentException("Invalid user credentials");
            }
            boolean isSecure = false; //TODO use this
            URI uri = new URI(address);
            if (uri.getScheme().equals("https")) {
                isSecure = true;
            }

            this.client = MbedClientBuilder.newBuilder().domain(clientCreds[0]).credentials(clientCreds[1], clientSecret)
                    .notifChannel(httpServletChannel)
                    .notifListener(new NotificationListenerImpl(endpointContainer)).build(new InetSocketAddress(uri.getHost(), uri.getPort()));

            readAllEndpoints();
            connected = true;
        } catch (MbedClientInitializationException | URISyntaxException e) {
            e.printStackTrace(); //TODO handle properly
        }
    }

    

    public MbedClient client() {
        return client;
    }

    public boolean isConnected() {
        return connected;
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
