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
import com.arm.mbed.restclient.MbedClientRuntimeException;
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
    private boolean connected;
    private EndpointContainer endpointContainer;
    public static final ServerConfiguration defaultServerConfiguration = new ServerConfiguration("http://localhost:8080", "domain/app2", "secret", null);

    @Inject
    public MbedClientService() {
        this(null);
    }

    public MbedClientService(MbedClient mbedClient) {
        this.endpointContainer = new EndpointContainer();
        if (mbedClient != null) {
            this.client = mbedClient;
            this.connected = true;
        } else {
            try {
                createConnection(defaultServerConfiguration);
            } catch (MbedClientInitializationException | URISyntaxException | MbedClientRuntimeException e) {
                // cannot create connection
            } catch (Throwable ex) {
                System.out.println("ex");
                ex.printStackTrace();
            }
        }
    }

    public final void createConnection(ServerConfiguration configuration) throws MbedClientInitializationException, URISyntaxException {
        createConnection(configuration.getAddress(), configuration.getUsername(), configuration.getPassword());
    }

    public final void createConnection(String address, String clientName, String clientSecret) throws MbedClientInitializationException, URISyntaxException {
        connected = false;
        String[] clientCreds = clientName.split("/");
        if (clientCreds.length != 2) {
            throw new MbedClientInitializationException("Invalid user credentials");
        }
        boolean isSecure = false; //TODO use this
        URI uri = new URI(address);
        if (uri.getScheme().equals("https")) {
            isSecure = true;
        }

        this.endpointContainer = new EndpointContainer();
        HttpServletChannel httpServletChannel = new HttpServletChannel(30, 2000);
        if (isSecure) {
            this.client = MbedClientBuilder.newBuilder().domain(clientCreds[0]).credentials(clientCreds[1], clientSecret)
                    .secure()
                    .notifChannel(httpServletChannel)
                    .notifListener(new NotificationListenerImpl(endpointContainer)).build(new InetSocketAddress(uri.getHost(), uri.getPort()));
        } else {
            this.client = MbedClientBuilder.newBuilder().domain(clientCreds[0]).credentials(clientCreds[1], clientSecret)
                    .notifChannel(httpServletChannel)
                    .notifListener(new NotificationListenerImpl(endpointContainer)).build(new InetSocketAddress(uri.getHost(), uri.getPort()));
        }
        readAllEndpoints();
        connected = true;
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
