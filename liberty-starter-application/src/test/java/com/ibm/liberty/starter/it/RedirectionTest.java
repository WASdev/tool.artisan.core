/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ibm.liberty.starter.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class RedirectionTest {


    @Test
    public void testStartSlashContextRoot() throws Exception {
        String queryString = "/start/";
        Response response = getResponse(queryString);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testStartContextRoot() throws Exception {
        String queryString = "/start";
        Response response = getResponse(queryString);
        try {
            assertEquals(302, response.getStatus());
            MultivaluedMap<String, Object> headers = response.getHeaders();
            String location = headers.get("Location").toString();
            String port = System.getProperty("liberty.test.port");
            String expectedLocation = "http://localhost:" + port + "/start/";
            assertTrue("Location should be /start/, instead got " + location, location.equals("[" + expectedLocation + "]"));
        } finally {
            response.close();
        }
    }

    @Test
    public void testSlashContextRoot() throws Exception {
        String queryString = "";
        Response response = getResponse(queryString);
        try {
            assertEquals(301, response.getStatus());
            MultivaluedMap<String, Object> headers = response.getHeaders();
            String location = headers.get("Location").toString();
            assertTrue("Location should be /start, instead got " + location, location.equals("[/start/]"));
        } finally {
            response.close();
        }
    }

    @Test
    public void testCheeseContextRoot() throws Exception {
        String queryString = "/cheese";
        Response response = getResponse(queryString);
        try {
            assertEquals(301, response.getStatus());
            MultivaluedMap<String, Object> headers = response.getHeaders();
            String location = headers.get("Location").toString();
            assertTrue("Location should be /start, instead got " + location, location.equals("[/start/]"));
        } finally {
            response.close();
        }
    }

    private Response getResponse(String queryString) {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + queryString;
        System.out.println("Testing " + url);
        Response getResponse = client.target(url).request("text/html").get();
        return getResponse;
    }

}
