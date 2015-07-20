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

import com.arm.mbed.restclient.endpoint.ResponseListener;
import com.arm.mbed.restclient.entity.EndpointResponse;
import com.arm.mbed.restclient.entity.notification.EndpointDescription;
import com.arm.mbed.restclient.entity.notification.ResourceInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.mbed.example.MbedClientService;
import org.mbed.example.data.EndpointMetadata;
import org.mbed.example.data.ResourceMetadata;
import org.mbed.example.data.ResourcePath;
import org.mbed.example.data.ResourceValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mitvah01 on 29.6.2015.
 */
@Path("/endpoints")
@Singleton
public final class EndpointsResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointsResource.class);
    private final MbedClientService clientCtr;

    @Inject
    public EndpointsResource(MbedClientService mbedClientService) {
        this.clientCtr = mbedClientService;
    }

    /**
     * Returns endpoints in Json format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<EndpointMetadata> getEndpoints() {
        //return only list with endpoint name and type
        return clientCtr.endpointContainer().getAllEndpoints().stream().map(EndpointMetadata::from).collect(Collectors.toList());
    }

    /**
     * Returns endpoints resources in Json format
     *
     * @param name String name of the endpoint
     */
    @GET
    @Path("{endpoint_name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ResourceMetadata> getEndpointResources(@PathParam("endpoint_name") String name) {
        EndpointDescription endpoint = clientCtr.endpointContainer().getEndpoint(name);
        if (endpoint == null) {
            //endpoint does not exists
            throw new NotFoundException();
        }

        ResourceInfo[] resourceList = endpoint.getResources();
        if (resourceList == null) {
            //resource list is missing, try to reload it
            resourceList = clientCtr.endpointContainer().updateResourceList(name, clientCtr.client().endpoint(name).readResourceList());
            if (resourceList == null) {
                //still missing
                return Collections.emptyList();
            }
        }

        return Arrays.stream(resourceList).map(r -> ResourceMetadata.from(r, isSubscribed(/* r */))).collect(Collectors.toList());
    }

    private boolean isSubscribed(/* ResourceInfo resourceInfo */) {
        //TODO: Check if it is subscribed or not
        return false;
    }

    @GET
    @Path("/{endpoint_name}/request/{resource-path : .+}")
    public void invokeProxyRequest(@PathParam("endpoint_name") String name, @PathParam("resource-path") String path) throws ClientErrorException {
        //initiate request
        final ResourcePath resourcePath = new ResourcePath(name, "/" + path);
        if (!clientCtr.endpointContainer().updateResource(resourcePath, true)) {
            throw new ClientErrorException("Only one reqeust at a time allowed.", 409);
        }

        LOGGER.debug("Making request GET {}", resourcePath);
        clientCtr.client().endpoint(name).resource("/" + path).get(new ResponseListener<EndpointResponse>() {
            @Override
            public void onResponse(EndpointResponse response) {
                clientCtr.endpointContainer().updateResource(resourcePath, response);
            }

            @Override
            public void onError(Exception ex) {
                clientCtr.endpointContainer().updateResource(resourcePath, ex.getMessage());
            }

            @Override
            public void onAsyncIdResponse() {
                //ignore
            }
        });
    }

    @GET
    @Path("/{endpoint-name}/values")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<ResourcePath, ResourceValue> getResourceValues(@PathParam("endpoint-name") String endpointName) {
        return clientCtr.endpointContainer().getEndpointResourceValues(endpointName);
    }

    //TODO: add PUT, POST, DELETE
    //    @PUT
    //    @Path("{endpoint_name}/{resource-path: .+}")
    //    @Produces(MediaType.TEXT_PLAIN)
    //    public String putResourcesValue(@QueryParam("value") String value, @PathParam("endpoint_name") String name
    //            , @PathParam("resource-path") String path) {
    //        try {
    //            return clientCtr.client().endpoint(name).resource(path).get().get().getPayloadAsString();
    //        } catch (InterruptedException e) {
    //            e.printStackTrace();
    //        } catch (ExecutionException e) {
    //            e.printStackTrace();
    //        }
    //        return null;
    //    }

}
