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

import com.arm.mbed.restclient.entity.Endpoint;
import com.arm.mbed.restclient.entity.EndpointResponse;
import com.arm.mbed.restclient.entity.ResourceDescription;
import com.arm.mbed.restclient.entity.notification.EndpointDescription;
import com.arm.mbed.restclient.entity.notification.ResourceInfo;
import com.arm.mbed.restclient.entity.notification.ResourceNotification;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.mbed.example.common.string.Utf8String;
import org.mbed.example.data.ResourcePath;
import org.mbed.example.data.ResourceValue;
import org.mbed.example.resources.EndpointsResource;
import org.slf4j.LoggerFactory;

/**
 * Created by mitvah01 on 15.7.2015.
 */
public class EndpointContainer {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EndpointsResource.class);
    private final Map<String, EndpointDescription> endpointsList = new ConcurrentHashMap<>();
    private final Map<ResourcePath, ResourceValue> endpointResourceValues = new ConcurrentHashMap<>();

    public Collection<EndpointDescription> getAllEndpoints() {
        return endpointsList.values();
    }

    public void putEndpoints(EndpointDescription[] endpoints) {
        for (EndpointDescription endpointDescription : endpoints) {
            endpointsList.put(endpointDescription.getName(), endpointDescription);
        }
    }

    public void removeEndpoints(String[] endpointsRemoved) {
        for (String endpoint : endpointsRemoved) {
            endpointsList.remove(endpoint);
        }
    }

    public void updateEndpointsList(List<Endpoint> endpoints) {
        endpointsList.clear();
        for (Endpoint ep : endpoints) {
            endpointsList.put(ep.getName(), new EndpointDescription(ep.getName(), ep.getType(), ep.isQueueMode(), null));
        }
    }

    public boolean updateResource(ResourcePath resourcePath, boolean isWaitingForResponse) {
        final AtomicBoolean success = new AtomicBoolean(true);
        endpointResourceValues.compute(resourcePath, (resourcePath1, old) -> {
            if (old != null) {
                if (old.isWaitingForResponse()) {
                    success.set(false);
                    return old;
                }
                return new ResourceValue(old.getValue(), isWaitingForResponse, 0, null, null, 0, false);
            } else {
                return new ResourceValue(null, isWaitingForResponse, 0, null, null, 0, false);
            }
        });
        return success.get();
    }

    public void updateResource(ResourcePath resourcePath, EndpointResponse response) {
        endpointResourceValues.put(resourcePath, new ResourceValue(response.getPayloadAsString(), false, response.getStatus(), null, response.getContentType(), response.getMaxAge(), false));
    }

    public void updateResource(ResourcePath resourcePath, ResourceNotification notification) {
        endpointResourceValues.put(resourcePath, new ResourceValue(Utf8String.from(notification.getPayload()), false, 200, null, notification.getContentType(), (int) notification.getMaxAge(), true));
    }
    public void updateResource(ResourcePath resourcePath, EndpointResponse response, String value) {
        String payloadAsString = response.getPayloadAsString();
        if (payloadAsString != null && !payloadAsString.isEmpty()) {
            if (response.getStatus()==200) {
                endpointResourceValues.put(resourcePath, new ResourceValue(payloadAsString, false, response.getStatus(), null, response.getContentType(), response.getMaxAge(), false));
            } else {
                endpointResourceValues.put(resourcePath, new ResourceValue(null, false, response.getStatus(), payloadAsString, response.getContentType(), response.getMaxAge(), false));
            }
        } else {
            endpointResourceValues.put(resourcePath, new ResourceValue(value, false, response.getStatus(), null, response.getContentType(), response.getMaxAge(), false));
        }
    }

    public void updateResource(ResourcePath resourcePath, String errorMessage) {
        endpointResourceValues.put(resourcePath, new ResourceValue(null, false, 0, errorMessage, null, 0, false));
    }
    public Map<ResourcePath, ResourceValue> getEndpointResourceValues(String endpointName) {
        EndpointDescription endpointDescription = endpointsList.get(endpointName);
        if (endpointDescription == null) {
            LOGGER.warn("Endpoint " + endpointName + " is missing");
            return null;
        }
        if (endpointDescription.getResources() == null) {
            LOGGER.warn("Resource list is missing, nothing to return. [Endpoint name: " + endpointName + "]");
            return null;
        }

        Map<ResourcePath, ResourceValue> valueMap = new HashMap<>();
        for (ResourceInfo resource : endpointDescription.getResources()) {
            ResourcePath resourcePath = new ResourcePath(endpointName, resource.getPath());
            ResourceValue resourceValue = endpointResourceValues.get(resourcePath);
            if (resourceValue != null) {
                valueMap.put(resourcePath, resourceValue);
            }
        }
        return valueMap;
    }

    public EndpointDescription getEndpoint(String name) {
        return this.endpointsList.get(name);
    }

    public ResourceInfo[] updateResourceList(String epName, List<ResourceDescription> resourceList) {
        ResourceInfo[] resources = resourceList.stream().map(resourceDescription ->
                new ResourceInfo(resourceDescription.getUriPath(), resourceDescription.getInterfaceDescription(), resourceDescription.getResourceType(),
                        resourceDescription.getType(), resourceDescription.isObservable())).collect(Collectors.toList()).toArray(new ResourceInfo[resourceList.size()]);

        EndpointDescription endpointDescription = endpointsList.compute(epName, (s, old) -> {
            if (old != null) {
                LOGGER.trace("Updated resources for endpoint: {}", epName);
                return new EndpointDescription(old.getName(), old.getType(), old.getQueueMode(), resources);
            }
            return null;
        });
        if (endpointDescription != null) {
            return endpointDescription.getResources();
        } else {
            return null;
        }
    }
}
