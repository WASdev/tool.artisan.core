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

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

public class MicroProfileAPIJarDownloadTest {

    @Test
    public void testRepoUrl() throws Exception {
        String url = "http://localhost:" + System.getProperty("liberty.test.port") + "/start/api/v1/repo/net/wasdev/wlp/starters/microprofile-apis/config/0.1/config-0.1.jar";
        Client client = ClientBuilder.newClient();
        Response response = client.target(url).request("application/zip").get();
        try {
            int responseStatus = response.getStatus();
            if (responseStatus != 200) {
                Assert.fail("Expected response status 200, instead found " + responseStatus + ". Response was " + response.readEntity(String.class));
            }
            assertTrue("Config class not found", findConfig(response));
        } finally {
            response.close();
        }

    }

    private boolean findConfig(Response resp) throws Exception {
        // Read the response into an InputStream
        InputStream entityInputStream = resp.readEntity(InputStream.class);
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        // This system property is being set in the liberty-starter-application/build.gradle file
        String tempDir = System.getProperty("liberty.temp.dir");
        File file = new File(tempDir + "/TestApp.zip");
        System.out.println("Creating zip file: " + file.toString());
        file.getParentFile().mkdirs();
        ZipEntry inputEntry = null;
        boolean found = false;
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            String entryName = inputEntry.getName();
            System.out.println("File: "+entryName);
            if ("org/eclipse/microprofile/config/Config.class".equals(entryName)) {
                System.out.println("Found Config.class file.");
                found = true;
            }
            zipIn.closeEntry();
        }
        zipIn.close();
        return found;
    }
}
