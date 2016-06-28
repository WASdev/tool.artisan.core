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
package com.ibm.liberty.starter.service.springboot.api.v1.it;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EndpointTest {

	//use this to give the REST endpoint a chance to come up after the server has reported it's started
	public void checkAvailability(String endpoint) {
		String ep = getEndPoint(endpoint);
		for(int x = 0; x < 3; x++) {
			Response response = null;
	        try {
		        response = sendRequest(ep, "GET");
		        if(response.getStatus() == 200) {
		        	return;
		        }
	        } finally {
        		if(response != null) {
        			response.close();
        		}
	        }
	        try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				//just exit
			}
		}
	}
	
	private String getEndPoint(String endpoint) {
		String port = System.getProperty("liberty.test.port");
        String war = System.getProperty("war.name");
        return "http://localhost:" + port + "/" + war + endpoint;
	}
	
    @SuppressWarnings("unchecked")
	public <T> T testEndpoint(String endpoint, Class<?> entity) throws Exception {
        String url = getEndPoint(endpoint);
        System.out.println("Testing " + url);
        Response response = null;
        try {
	        response = sendRequest(url, "GET");
	        int responseCode = response.getStatus();
	        assertTrue("Incorrect response code for " + url + ": " + responseCode,
	                   responseCode == 200);
	        String json = response.readEntity(String.class);
	        ObjectMapper mapper = new ObjectMapper();
	       return (T) mapper.readValue(json, entity);       	
        } finally {
        	if(response != null) {
        		response.close();
        	}
        }
    }

    public Response sendRequest(String url, String requestType) {
        Client client = ClientBuilder.newClient();
        System.out.println("Testing " + url);
        WebTarget target = client.target(url);
        Invocation.Builder invoBuild = target.request();
        Response response = invoBuild.build(requestType).invoke();
        return response;
    }
}
