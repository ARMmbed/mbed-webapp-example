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

import com.arm.mbed.restclient.MbedClient;
import com.arm.mbed.restclient.MbedClientBuilder;
import com.arm.mbed.restclient.MbedClientInitializationException;
import com.arm.mbed.restclient.endpoint.Entity;
import com.arm.mbed.restclient.entity.Endpoint;
import com.arm.mbed.restclient.entity.EndpointResponse;
import com.arm.mbed.restclient.entity.ResourceDescription;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitvah01 on 29.6.2015.
 */
@Path("/endpoints")
@Singleton
public class EndpointResources {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointResources.class);
    private final MbedClient client;
    private final EndpointContainer endpointContainer = new EndpointContainer();
    private final Map<String, Endpoint> endpointsList = new HashMap<>();

    @Inject
    public EndpointResources() throws MbedClientInitializationException {
        this.client = MbedClientBuilder.newBuilder().domain("domain").credentials("app2", "secret")
                .notifChannelHttpServer(0)
                .notifListener(endpointContainer).build(8080);
        fillEndpointContainer();
    }

    private void fillEndpointContainer() {
        //todo list = client.endpoints().readAll();
        for (Endpoint endpoint : client.endpoints().readAll()) {
            endpointsList.put(endpoint.getName(), endpoint);
        }
        endpointContainer.setEndpointsList(endpointsList);
    }

    EndpointResources(MbedClient mbedClient) {
        this.client = mbedClient;
        fillEndpointContainer();
    }

    /**
     * Returns endpoints in Json format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Endpoint> getEndpoints() {
        return endpointContainer.getEndpointsList().values();
    }

    /**
     * Returns endpoints resources in Json format
     *
     * @param name String name of the endpoint
     */
    @GET
    @Path("{endpoint_name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List getEndpointResources(@PathParam("endpoint_name") String name) {
        List<ResourceDescription> resources = client.endpoint(name).readResourceList();
        List returnList = new ArrayList<>();
        for (ResourceDescription resourceDescription : resources) {
            Resources resource = new Resources();
            resource.setUri(resourceDescription.getUriPath());
            resource.setIfDesc(resourceDescription.getInterfaceDescription());
            //            TODO: Check if it is subscribed or not
            resource.setRt(resourceDescription.getType());
            resource.setType(resourceDescription.getType());
            //            TODO: Get the value of the resources
            resource.setVal("TODO");
            returnList.add(resource);
            LOGGER.debug("resource details: " + resource.toString());
        }
        return returnList;
    }

    @GET
    @Path("{endpoint_name}/{resource-path : .+}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getResourcesValue(@PathParam("endpoint_name") String name
            , @PathParam("resource-path") String path) {
        String endpointResponse = "";
        try {
            endpointResponse = client.endpoint(name).resource(path).get().get().getPayloadAsString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("first: " + endpointResponse);
        return endpointResponse;
    }

    @PUT
    @Path("{endpoint_name}/{resource-path: .+}")
    @Produces(MediaType.TEXT_PLAIN)
    public String putResourcesValue(@QueryParam("value") String value, @PathParam("endpoint_name") String name
            , @PathParam("resource-path") String path) {
        EndpointResponse endpointResponse = null;
        System.out.println("value: " + value + "name:" + name + "path" + path);
        String response = "";
        try {
            endpointResponse = client.endpoint(name).resource(path).put(Entity.text(value)).get();
            response = client.endpoint(name).resource(path).get().get().getPayloadAsString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return response;
    }

}
