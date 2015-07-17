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

import com.arm.mbed.restclient.endpoint.ResponseListener;
import com.arm.mbed.restclient.entity.EndpointResponse;
import org.slf4j.LoggerFactory;

/**
 * Created by mitvah01 on 16.7.2015.
 */
class EndpointResponseResponseListener implements ResponseListener<EndpointResponse> {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EndpointResources.class);
    private EndpointResponse endpointResponse;

    public EndpointResponse getEndpointResponse() {
        return endpointResponse;
    }

    public void setEndpointResponse(EndpointResponse response) {
        endpointResponse = response;
    }

    @Override
    public void onResponse(EndpointResponse response) {
        setEndpointResponse(response);
    }

    @Override
    public void onError(Exception ex) {
        LOGGER.debug("Received error " + ex.getMessage());
    }

    @Override
    public void onAsyncIdResponse() {
        LOGGER.debug("onAsyncIdResponse ");
    }
}
