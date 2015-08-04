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

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.mbed.example.MbedClientService;

/**
 * Created by mitvah01 on 3.8.2015.
 */
@Path("/mbedclient")
public class MbedClientResource {
    private final MbedClientService clientCtr;

    @Inject
    public MbedClientResource(MbedClientService mbedClientService) {
        this.clientCtr = mbedClientService;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public boolean isConnected() {
        return clientCtr.isConnected();
    }
}
