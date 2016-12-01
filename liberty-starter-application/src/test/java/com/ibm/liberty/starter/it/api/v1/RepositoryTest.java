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
package com.ibm.liberty.starter.it.api.v1;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;

public class RepositoryTest {
    
    @Test
    public void testTestProvidedPom() throws Exception {
        String file = callRepoEndpoint("net/wasdev/wlp/starters/test/provided-pom/0.0.1/provided-pom-0.0.1.pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters.test, contents of file was " + file, file.contains("<groupId>net.wasdev.wlp.starters.test</groupId>"));
        assertTrue("Expecting pom file with artifactId provided-pom, contents of file was " + file, file.contains("<artifactId>provided-pom</artifactId>"));
        assertTrue("Expecting pom file with version 0.0.1, contents of file was " + file, file.contains("<version>0.0.1</version>"));
    }
    
    @Test
    public void testTestMicroserviceRuntimePom() throws Exception {
        String file = callRepoEndpoint("net/wasdev/wlp/starters/test/runtime-pom/0.0.1/runtime-pom-0.0.1.pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters.test, contents of file was " + file, file.contains("<groupId>net.wasdev.wlp.starters.test</groupId>"));
        assertTrue("Expecting pom file with artifactId provided-pom, contents of file was " + file, file.contains("<artifactId>runtime-pom</artifactId>"));
        assertTrue("Expecting pom file with version 0.0.1, contents of file was " + file, file.contains("<version>0.0.1</version>"));
    }
    
    @Test
    public void testTestMicroserviceCompilePom() throws Exception {
        String file = callRepoEndpoint("net/wasdev/wlp/starters/test/compile-pom/0.0.1/compile-pom-0.0.1.pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters.test, contents of file was " + file, file.contains("<groupId>net.wasdev.wlp.starters.test</groupId>"));
        assertTrue("Expecting pom file with artifactId provided-pom, contents of file was " + file, file.contains("<artifactId>compile-pom</artifactId>"));
        assertTrue("Expecting pom file with version 0.0.1, contents of file was " + file, file.contains("<version>0.0.1</version>"));
    }
    
    @Test
    public void testTestMicroserviceServerSnippet() throws Exception {
        String file = callRepoEndpoint("net/wasdev/wlp/starters/test/server-snippet/0.0.1/server-snippet-0.0.1.xml", 200);
        assertTrue("Server snippet containing <!-- Sample test server snippet -->, contents of file was " + file, file.contains("<!-- Sample test server snippet -->"));
    }
    
    @Test
    @Ignore
    public void testServletProvidedPom() throws Exception {
        String file = callRepoEndpoint("net/wasdev/wlp/starters/web/provided-pom/0.0.1/provided-pom-0.0.1.pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters.web, contents of file was " + file, file.contains("<groupId>net.wasdev.wlp.starters.web</groupId>"));
        assertTrue("Expecting pom file with artifactId provided-pom, contents of file was " + file, file.contains("<artifactId>provided-pom</artifactId>"));
        assertTrue("Expecting pom file with version 0.0.1, contents of file was " + file, file.contains("<version>0.0.1</version>"));
    }
    
    @Test
    @Ignore
    public void testServletRuntimePom() throws Exception {
        String file = callRepoEndpoint("net/wasdev/wlp/starters/web/runtime-pom/0.0.1/runtime-pom-0.0.1.pom", 200);
        assertTrue("Expecting pom file with groupId net.wasdev.wlp.starters.web, contents of file was " + file, file.contains("<groupId>net.wasdev.wlp.starters.web</groupId>"));
        assertTrue("Expecting pom file with artifactId runtime-pom, contents of file was " + file, file.contains("<artifactId>runtime-pom</artifactId>"));
        assertTrue("Expecting pom file with version 0.0.1, contents of file was " + file, file.contains("<version>0.0.1</version>"));
    }
    
    @Test
    @Ignore
    public void testServletMetaData() throws Exception {
        String file = callRepoEndpoint("net/wasdev/wlp/starters/web/provided-pom/maven-metadata-local.xml", 200);
        assertTrue("Expecting xml file with tag <metadata>, contents of file was " + file, file.contains("<metadata>"));
    }
    
    @Test
    public void testInvalidFile() throws Exception {
        String url = "net/wasdev/wlp/starters/test/wibble/0.0.1/wibble-0.0.1.pom";
        String output = callRepoEndpoint(url, 404);
        assertTrue("Invalid file name, expected 404. Output was " + output, output.contains("File not found: " + url));
    }

    @Test
    public void testInvalidTechType() throws Exception {
        String url = "net/wasdev/wlp/starters/wibble/provided-pom/0.0.1/provided-pom-0.0.1.pom";
        String output = callRepoEndpoint(url, 404);
        assertTrue("Status 404 returned correctly but error message was incorrect. Expected 'Tech type wibble not found', was " + output, output.contains("Tech type wibble not found"));
    }

    private String callRepoEndpoint(String file, int expectedStatus) throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/repo/" + file;
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
        try {
            int responseStatus = response.getStatus();
            InputStream responseStream = response.readEntity(InputStream.class);
            String output = inputStreamToString(responseStream);
            assertTrue("Response status is: " + responseStatus + " Response message: " + output, responseStatus == expectedStatus);
            return output;
        } finally {
            response.close();
        }
    }
    
    private String inputStreamToString(InputStream inputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(inputStream);
        char[] chars = new char[1024];
        StringBuilder responseBuilder = new StringBuilder();

        int read;
        while ((read = isr.read(chars)) != -1) {
            responseBuilder.append(chars, 0, read);
        }
        return responseBuilder.toString();
    }

}
