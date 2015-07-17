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

import com.arm.mbed.restclient.NotificationListener;
import com.arm.mbed.restclient.entity.Endpoint;
import com.arm.mbed.restclient.entity.EndpointStatus;
import com.arm.mbed.restclient.entity.notification.EndpointDescription;
import com.arm.mbed.restclient.entity.notification.ResourceNotification;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 * Created by mitvah01 on 15.7.2015.
 */
class EndpointContainer implements NotificationListener {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EndpointResources.class);
    private Map<String, Endpoint> endpointsList = new HashMap<>();

    public Map<String, Endpoint> getEndpointsList() {
        return endpointsList;
    }

    public void setEndpointsList(Map<String, Endpoint> initialList) {
        endpointsList = initialList;
    }

    @Override
    public void onEndpointsRegistered(EndpointDescription[] endpoints) {
        for (EndpointDescription endpointDescription : endpoints) {
            Endpoint endpoint = new Endpoint(endpointDescription.getName()
                    , endpointDescription.getType(), EndpointStatus.ACTIVE, endpointDescription.getQueueMode());
            endpointsList.put(endpoint.getName(), endpoint);
        }
    }

    @Override
    public void onEndpointsUpdated(EndpointDescription[] endpoints) {
        for (EndpointDescription endpointDescription : endpoints) {
            Endpoint endpoint = new Endpoint(endpointDescription.getName()
                    , endpointDescription.getType(), EndpointStatus.ACTIVE, endpointDescription.getQueueMode());
            if (endpointsList.containsKey(endpoint.getName())) {
                endpointsList.replace(endpoint.getName(), endpoint);
            } else {
                endpointsList.put(endpoint.getName(), endpoint);
            }
        }
    }

    @Override
    public void onEndpointsRemoved(String[] endpointsRemoved) {
        for (String endpoint : endpointsRemoved) {
            endpointsList.remove(endpoint);
        }
    }

    @Override
    public void onResourcesUpdated(ResourceNotification[] resourceNotifications) {
        LOGGER.debug("Receive notification - resource updated: " + resourceNotifications.length);
    }

    @Override
    public void onEndpointsExpired(String[] endpointsExpired) {
        for (String endpoint : endpointsExpired) {
            endpointsList.remove(endpoint);
        }
    }
}
