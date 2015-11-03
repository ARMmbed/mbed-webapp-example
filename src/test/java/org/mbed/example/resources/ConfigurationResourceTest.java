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

import static org.mockito.Mockito.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mbed.example.MbedClientService;
import org.mbed.example.data.ServerConfiguration;

/**
 * Created by mitvah01 on 4.9.2015.
 */
public class ConfigurationResourceTest extends TestCase {
    private ConfigurationResource rest;
    private MbedClientService mbedClientService;

    @Before
    public void setUp() throws Exception {
        mbedClientService = mock(MbedClientService.class);
        rest = new ConfigurationResource(mbedClientService);
    }

    @Test
    public void testGetConfiguration() throws Exception {
        ServerConfiguration serverConfiguration = MbedClientService.DEFAULT_SERVER_CONFIGURATION;
        assertEquals(serverConfiguration, rest.getConfiguration());
    }

    @Test
    public void testSetConfiguration() throws Exception {

        ServerConfiguration serverConfiguration = new ServerConfiguration();

        serverConfiguration.setUsername("domain/app2");
        serverConfiguration.setPassword("secret");
        serverConfiguration.setAddress("http://localhost:8080");
        rest.setConfiguration(serverConfiguration);
        assertEquals("domain/app2", rest.getConfiguration().getUsername());
        assertEquals("secret", rest.getConfiguration().getPassword());
        assertEquals("http://localhost:8080", rest.getConfiguration().getAddress());

        serverConfiguration.setToken("UR35WWWS5CBAQ4SCTOZPF6XY9K0YSTU1NP6LMTLR");
        rest.setConfiguration(serverConfiguration);
        assertEquals("Bearer UR35WWWS5CBAQ4SCTOZPF6XY9K0YSTU1NP6LMTLR", rest.getConfiguration().getToken());

        serverConfiguration.setToken("Bearer UR35WWWS5CBAQ4SCTOZPF6XY9K0YSTU1NP6LMTLR");
        rest.setConfiguration(serverConfiguration);
        assertEquals("Bearer UR35WWWS5CBAQ4SCTOZPF6XY9K0YSTU1NP6LMTLR", rest.getConfiguration().getToken());

    }
}