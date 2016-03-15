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

import static org.assertj.core.api.StrictAssertions.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.arm.mbed.restclient.MbedClient;
import com.arm.mbed.restclient.endpoint.Entity;
import com.arm.mbed.restclient.endpoint.PreSubscriptionEntry;
import com.arm.mbed.restclient.endpoint.ResponseListener;
import com.arm.mbed.restclient.entity.Endpoint;
import com.arm.mbed.restclient.entity.EndpointResponse;
import com.arm.mbed.restclient.entity.ResourceDescription;
import java.util.Arrays;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mbed.example.MbedClientService;
import org.mbed.example.data.ResourcePath;
import org.mbed.example.data.ResourceValue;
import org.mockito.ArgumentCaptor;

/**
 * Created by mitvah01 on 13.7.2015.
 */
public class EndpointsResourceTest {

    private MbedClient mbedClient;
    private EndpointsResource rest;
    private MbedClientService mbedClientService;

    @Before
    public void setUp() throws Exception {
        mbedClient = mock(MbedClient.class, RETURNS_DEEP_STUBS);

        mbedClientService = new MbedClientService(mbedClient);
        rest = new EndpointsResource(mbedClientService);
    }

    @Test
    public void getEndpoints() {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        mbedClientService.readAllEndpoints();

        assertEquals(1, rest.getEndpoints().size());
    }

    @Test
    public void getEndpointResources() {

        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", "light", null, false),
                new Endpoint("dev-02", "light", null, false)));

        //test subscription by endpoint name
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry("dev-01", null, Arrays.asList())));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/dev/mac", null, null, null, true),
                new ResourceDescription("/s/temp", null, null, null, true)));
        when(mbedClient.endpoint("dev-02").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/dev/mac", null, null, null, true),
                new ResourceDescription("/s/temp", null, null, null, true)));

        mbedClientService.readAllEndpoints();
        assertEquals("/dev/mac", rest.getEndpointResources("dev-01").get(0).getUriPath());
        assertEquals(2, rest.getEndpointResources("dev-01").size());
        assertEquals(2, rest.getEndpointResources("dev-02").size());
        //non existing
        assertThatThrownBy(() -> rest.getEndpointResources("non-existing")).isInstanceOf(NotFoundException.class);

        assertEquals(true, rest.getEndpointResources("dev-01").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-01").get(1).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-02").get(0).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-02").get(1).isSubscribed());

        //test subscription by endpoint type
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry(null, "light", Arrays.asList())));

        assertEquals(true, rest.getEndpointResources("dev-01").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-01").get(1).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(1).isSubscribed());

        //test subscription by endpoint path
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry(null, null, Arrays.asList("/dev/mac"))));

        assertEquals(true, rest.getEndpointResources("dev-01").get(0).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-01").get(1).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(0).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-02").get(1).isSubscribed());

        //test subscription by endpoint name, type and path
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry("dev-01", "light", Arrays.asList("/dev/mac"))));

        assertEquals(true, rest.getEndpointResources("dev-01").get(0).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-01").get(1).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-02").get(0).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-02").get(1).isSubscribed());

        //test subscription by asterisk endpoint name
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry("dev*", null, Arrays.asList())));

        assertEquals(true, rest.getEndpointResources("dev-01").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-01").get(1).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(1).isSubscribed());

        //test subscription by asterisk endpoint type
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry(null, "li*", Arrays.asList())));

        assertEquals(true, rest.getEndpointResources("dev-01").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-01").get(1).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(1).isSubscribed());

        //test subscription by asterisk endpoint path
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry(null, null, Arrays.asList("/dev/*"))));

        assertEquals(true, rest.getEndpointResources("dev-01").get(0).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-01").get(1).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(0).isSubscribed());
        assertEquals(false, rest.getEndpointResources("dev-02").get(1).isSubscribed());

        //test subscription by two asterisk endpoint path
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry(null, null, Arrays.asList("/dev/*,/s/*"))));

        assertEquals(true, rest.getEndpointResources("dev-01").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-01").get(1).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(0).isSubscribed());
        assertEquals(true, rest.getEndpointResources("dev-02").get(1).isSubscribed());

    }

    @Test
    public void invokeProxyRequest_success() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke GET proxy request
        rest.invokeProxyRequest("dev-01", "temp");

        //read values, no response available
        assertTrue(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")).isWaitingForResponse());

        //response returns from mDS
        mockResponseListener_onResponse("dev-01", "/temp", "24C", 200);

        //read values
        assertEquals(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")),
                new ResourceValue("24C", false, 200, null, null, 0, false));
    }

    @Test
    public void invokeProxyRequest_duplicate() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke GET proxy request
        rest.invokeProxyRequest("dev-01", "temp");

        //again invoke GET proxy request for same resource
        assertThatThrownBy(() -> rest.invokeProxyRequest("dev-01", "temp")).isInstanceOf(ClientErrorException.class);
    }

    @Test
    public void invokeProxyRequest_withError() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke GET proxy request
        rest.invokeProxyRequest("dev-01", "temp");

        //response returns from mDS
        mockResponseListener_onResponse("dev-01", "/temp", new Exception("Error while reading!"));

        //read values
        assertEquals(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")),
                new ResourceValue(null, false, 0, "Error while reading!", null, 0, false));

    }

    private void mockResponseListener_onResponse(String endpointName, String path, String payload, int status) {
        ArgumentCaptor<ResponseListener> respCapture = ArgumentCaptor.forClass(ResponseListener.class);
        verify(mbedClient.endpoint(endpointName).resource(path)).get(respCapture.capture());
        respCapture.getValue().onAsyncIdResponse();

        respCapture.getValue().onResponse(mockEndpointResponse(payload, status));
    }

    private void mockResponseListener_put_onResponse(String endpointName, String path, String payload, int status) {
        ArgumentCaptor<ResponseListener> respCapture = ArgumentCaptor.forClass(ResponseListener.class);
        ArgumentCaptor<Entity> entCapture = ArgumentCaptor.forClass(Entity.class);
        verify(mbedClient.endpoint(endpointName).resource(path)).put(entCapture.capture(), respCapture.capture());
        respCapture.getValue().onAsyncIdResponse();

        respCapture.getValue().onResponse(mockEndpointResponse(payload, status));
    }

    private void mockResponseListener_post_onResponse(String endpointName, String path, String payload, int status) {
        ArgumentCaptor<ResponseListener> respCapture = ArgumentCaptor.forClass(ResponseListener.class);
        ArgumentCaptor<Entity> entCapture = ArgumentCaptor.forClass(Entity.class);
        verify(mbedClient.endpoint(endpointName).resource(path)).post(entCapture.capture(), respCapture.capture());
        respCapture.getValue().onAsyncIdResponse();

        respCapture.getValue().onResponse(mockEndpointResponse(payload, status));
    }
    private void mockResponseListener_delete_onResponse(String endpointName, String path, String payload, int status) {
        ArgumentCaptor<ResponseListener> respCapture = ArgumentCaptor.forClass(ResponseListener.class);
        verify(mbedClient.endpoint(endpointName).resource(path)).delete(respCapture.capture());
        respCapture.getValue().onAsyncIdResponse();

        respCapture.getValue().onResponse(mockEndpointResponse(payload, status));
    }
    private void mockResponseListener_delete_onResponse(String endpointName, String path, Exception exception) {
        ArgumentCaptor<ResponseListener> respCapture = ArgumentCaptor.forClass(ResponseListener.class);
        verify(mbedClient.endpoint(endpointName).resource(path)).delete(respCapture.capture());
        respCapture.getValue().onError(exception);
    }

    public static EndpointResponse mockEndpointResponse(String payload, int status) {
        EndpointResponse endpointResponse = mock(EndpointResponse.class);
        when(endpointResponse.getPayloadAsString()).thenReturn(payload);
        when(endpointResponse.getStatus()).thenReturn(status);
        return endpointResponse;
    }

    public static EndpointResponse mockEndpointResponse(byte[] payload, int status, String contentType) {
        EndpointResponse endpointResponse = mock(EndpointResponse.class);
        when(endpointResponse.getPayload()).thenReturn(payload);
        when(endpointResponse.getStatus()).thenReturn(status);
        when(endpointResponse.getContentType()).thenReturn(contentType);
        return endpointResponse;
    }

    private void mockResponseListener_onResponse(String endpointName, String path, Exception exception) {
        ArgumentCaptor<ResponseListener> respCapture = ArgumentCaptor.forClass(ResponseListener.class);
        verify(mbedClient.endpoint(endpointName).resource(path)).get(respCapture.capture());
        respCapture.getValue().onError(exception);
    }
    @Test
    public void putResourcesValue_success() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke PUT request
        rest.putResourcesValue("32C", "dev-01", "temp");

        //get values, no response available
        assertTrue(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")).isWaitingForResponse());

        //response returns from mDS
        mockResponseListener_put_onResponse("dev-01", "/temp", "32C", 200);

        //read values
        assertEquals(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")),
                new ResourceValue("32C", false, 200, null, null, 0, false));
    }

    @Test
    public void putResourcesValue_withError() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke PUT request
        rest.putResourcesValue("32C", "dev-01", "temp");

        //response returns from mDS
        mockResponseListener_put_onResponse("dev-01", "/temp", "error", 405);

        //read values
        assertEquals(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")),
                new ResourceValue(null, false, 405, "error", null, 0, false));
    }
    @Test
    public void putResourcesValue_duplicate() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke PUT proxy request
        rest.putResourcesValue("32C", "dev-01", "temp");

        //again invoke GET proxy request for same resource
        assertThatThrownBy(() -> rest.putResourcesValue("32C", "dev-01", "temp")).isInstanceOf(ClientErrorException.class);
    }
    @Test
    public void postResourcesValue_success() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke POST request
        rest.postResourcesValue("32C", "dev-01", "temp");

        //get values, no response available
        assertTrue(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")).isWaitingForResponse());

        //response returns from mDS
        mockResponseListener_post_onResponse("dev-01", "/temp", "32C", 200);

        //read values
        assertEquals(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")),
                new ResourceValue("32C", false, 200, null, null, 0, false));
    }

    @Test
    public void postResourcesValue_withError() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke POST request
        rest.postResourcesValue("32C", "dev-01", "temp");

        //response returns from mDS
        mockResponseListener_post_onResponse("dev-01", "/temp", "error", 405);

        //read values
        assertEquals(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")),
                new ResourceValue(null, false, 405, "error", null, 0, false));
    }
    @Test
    public void postResourcesValue_duplicate() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke POST proxy request
        rest.postResourcesValue("32C", "dev-01", "temp");

        //again invoke GET proxy request for same resource
        assertThatThrownBy(() -> rest.postResourcesValue("32C", "dev-01", "temp")).isInstanceOf(ClientErrorException.class);
    }
    @Test
    public void deleteResourcesValue_success() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke DELETE request
        rest.deleteResourcesValue("dev-01", "/temp");

        //get values, no response available
        assertTrue(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")).isWaitingForResponse());

        //response returns from mDS
        mockResponseListener_delete_onResponse("dev-01", "/temp", null, 200);

        //read values
        assertEquals(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")),
                new ResourceValue(null, false, 200, null, null, 0, false));
    }
    @Test
    public void deleteResourcesValue_withError() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke DELETE request
        rest.deleteResourcesValue("dev-01", "/temp");

        //response returns from mDS
        mockResponseListener_delete_onResponse("dev-01", "/temp", new Exception("Error while reading!"));

        //read values
        assertEquals(rest.getResourceValues("dev-01").get(new ResourcePath("dev-01", "/temp")),
                new ResourceValue(null, false, 0, "Error while reading!", null, 0, false));
    }
    @Test
    public void deleteResourcesValue_duplicate() throws Exception {
        when(mbedClient.endpoints().readAll()).thenReturn(Arrays.asList(new Endpoint("dev-01", null, null, false)));
        when(mbedClient.endpoint("dev-01").readResourceList()).thenReturn(Arrays.asList(new ResourceDescription("/temp", null, null, null, false)));
        mbedClientService.readAllEndpoints();

        //read resources
        assertEquals(1, rest.getEndpointResources("dev-01").size());

        //invoke DELETE proxy request
        rest.deleteResourcesValue("dev-01", "temp");

        //again invoke GET proxy request for same resource
        assertThatThrownBy(() -> rest.deleteResourcesValue("dev-01", "temp")).isInstanceOf(ClientErrorException.class);
    }

    @Test
    public void isSubscribed() throws Exception {
        when(mbedClient.preSubscriptions().read()).thenReturn(Arrays.asList(new PreSubscriptionEntry("dev-01", null, Arrays.asList("/s/*")),
                new PreSubscriptionEntry("dev-01", null, Arrays.asList("power*"))));
        SubscriptionResources subscriptionResources = new SubscriptionResources(mbedClientService);
        assertEquals(2, subscriptionResources.getSubscriptions().size());


        //        assertEquals(1, rest.getEndpointResources("dev-01").size());
        //        System.out.println("rest.getEndpointResources(\"dev-01\") = " + rest.getEndpointResources("dev-01"));
        //        assertEquals("/dev/mac", rest.getEndpointResources("dev-01").get(0).getUriPath());
        //
        //        //non existing
        //        assertThatThrownBy(() -> rest.getEndpointResources("non-existing")).isInstanceOf(NotFoundException.class);
    }
}