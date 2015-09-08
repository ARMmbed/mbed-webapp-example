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

import java.net.ConnectException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.mbed.example.MbedClientService;
import org.mbed.example.data.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KALLE
 */
@Path("/configuration")
@Singleton
public final class ConfigurationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationResource.class);
    private final MbedClientService clientCtr;
    private ServerConfiguration serverConfiguration;

    @Inject
    public ConfigurationResource(MbedClientService mbedClientService) {
        this.clientCtr = mbedClientService;
        this.serverConfiguration = MbedClientService.DEFAULT_SERVER_CONFIGURATION;
    }

    /**
     * Returns Server Configuration
     *
     * @return server configuration
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ServerConfiguration getConfiguration() {
        LOGGER.debug("Reading server configuration.");
        return serverConfiguration;
    }

    /**
     * Sets configuration
     *
     * @param newConf new configuration
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void setConfiguration(ServerConfiguration newConf) {
        LOGGER.debug("Writing server configuration.");
        this.serverConfiguration = newConf;
        try {
            String token = newConf.getToken();
            if (token != null && !token.trim().contains(" ")) {
                newConf.setToken("bearer " + token.trim());
            }
            clientCtr.createConnection(serverConfiguration);
        } catch (Exception e) {
            String message;
            if (e.getCause() instanceof ConnectException) {
                message = "Cannot connect to Server";
            } else {
                message = e.getMessage();
            }
            throw new BadRequestException(Response.status(Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build(), e);
        }
    }
}
