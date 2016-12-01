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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PayloadTest {

    private int dependencySize = 0;
    private String artifacts = "";
    private String groups = "";
    private String repoUrls = "";
    private int responseStatus;
    private Object contentDisposition;

    @Test
    public void testTestMicroservice() throws Exception {
        String queryString = "tech=test&name=TestApp&deploy=local";
        callDataEndpoint(queryString);
        assertTrue("Expected net.wasdev.wlp.starters.test groupId. Found " + groups, groups.contains("net.wasdev.wlp.starters.test"));
        assertTrue("Expected provided-pom artifact. Found " + artifacts, artifacts.contains("provided-pom"));
        assertTrue("Expected runtime-pom artifact. Found " + artifacts, artifacts.contains("runtime-pom"));
        assertTrue("Expected compile-pom artifact. Found " + artifacts, artifacts.contains("compile-pom"));
        assertTrue("Expected junit artifact. Found " + artifacts, artifacts.contains("junit"));
        assertTrue("Expected CXF artifact. Found " + artifacts, artifacts.contains("cxf-rt-rs-client"));
        assertTrue("Expected glassfish artifact. Found " + artifacts, artifacts.contains("javax.json"));
        assertEquals(6, dependencySize);
    }

    @Test
    public void testRepoUrl() throws Exception {
        String queryString = "tech=test&name=TestApp&deploy=local";
        String expectedUrl = "http://localhost:" + System.getProperty("liberty.test.port") + "/start/api/v1/repo";
        callDataEndpoint(queryString);
        assertTrue("Expected repo url to be:" + expectedUrl + ", instead found:" + repoUrls, repoUrls.contains(expectedUrl));
    }

    @Test
    @Ignore
    public void testBoth() throws Exception {
        String queryString = "name=rest&name=websocket";
        callDataEndpoint(queryString);
        assertTrue("Expected net.wasdev.wlp.starters.rest groupId. Found " + groups, groups.contains("net.wasdev.wlp.starters.rest"));
        assertTrue("Expected net.wasdev.wlp.starters.websocket groupId. Found " + groups, groups.contains("net.wasdev.wlp.starters.websocket"));
        assertTrue("Expected dependency size of 4. Found " + dependencySize, dependencySize == 4);
    }

    @Test
    @Ignore
    public void testREST() throws Exception {
        String queryString = "name=rest";
        callDataEndpoint(queryString);
        assertTrue("Expected net.wasdev.wlp.starters.rest groupId. Found " + groups, groups.contains("net.wasdev.wlp.starters.rest"));
        assertTrue("Expected provided-pom artifact. Found " + artifacts, artifacts.contains("provided-pom"));
        assertTrue("Expected runtime-pom artifact. Found " + artifacts, artifacts.contains("runtime-pom"));
        assertTrue("Expected dependency size of 2. Found " + dependencySize, dependencySize == 2);
    }

    @Test
    @Ignore
    public void testWebsockets() throws Exception {
        String queryString = "name=websocket";
        callDataEndpoint(queryString);
        assertTrue("Expected net.wasdev.wlp.starters.websocket groupId. Found " + groups, groups.contains("net.wasdev.wlp.starters.websocket"));
        assertTrue("Expected provided-pom artifact. Found " + artifacts, artifacts.contains("provided-pom"));
        assertTrue("Expected runtime-pom artifact. Found " + artifacts, artifacts.contains("runtime-pom"));
        assertTrue(dependencySize == 2);
    }

    @Test
    @Ignore
    public void testWeb() throws Exception {
        String queryString = "name=web";
        callDataEndpoint(queryString);
        assertTrue("Expected net.wasdev.wlp.starters.web groupId. Found " + groups, groups.contains("net.wasdev.wlp.starters.web"));
        assertTrue("Expected provided-pom artifact. Found " + artifacts, artifacts.contains("provided-pom"));
        assertTrue("Expected runtime-pom artifact. Found " + artifacts, artifacts.contains("runtime-pom"));
        assertTrue(dependencySize == 2);
    }

    @Test
    public void testBase() throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?tech=test&name=Test&deploy=local";
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        try {
            responseStatus = response.getStatus();
            contentDisposition = response.getHeaders().get("Content-Disposition");
            assertTrue("Response status is: " + responseStatus, this.responseStatus == 200);
            // Read the response into an InputStream
            InputStream entityInputStream = response.readEntity(InputStream.class);
            // Create a new ZipInputStream from the response InputStream
            ZipInputStream zipIn = new ZipInputStream(entityInputStream);
            // This system property is being set in the liberty-starter-application/build.gradle file
            String tempDir = System.getProperty("liberty.temp.dir");
            File file = new File(tempDir + "/LibertyProject.zip");
            System.out.println("Creating zip file: " + file.toString());
            file.getParentFile().mkdirs();
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));
            ZipEntry inputEntry = null;
            boolean pomExists = false;
            while ((inputEntry = zipIn.getNextEntry()) != null) {
                String entryName = inputEntry.getName();
                zipOut.putNextEntry(new ZipEntry(entryName));
                if ("pom.xml".equals(entryName)) {
                    pomExists = true;
                }
            }
            zipOut.flush();
            zipIn.close();
            zipOut.close();
            System.out.println("Deleting file:" + file.toPath());
            Files.delete(file.toPath());
            assertTrue(pomExists);
        } finally {
            response.close();
        }
    }

    private void callDataEndpoint(String queryString) throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?" + queryString;
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        try {
            responseStatus = response.getStatus();
            contentDisposition = response.getHeaders().get("Content-Disposition");
            if (this.responseStatus != 200) {
                Assert.fail("Expected response status 200, instead found " + responseStatus + ". Response was " + response.readEntity(String.class));
            }
            parseResponse(response);
            System.out.println("Content disposition: " + contentDisposition);
            System.out.println("Groups: " + groups);
            System.out.println("Artifacts: " + artifacts);
        } finally {
            response.close();
        }

    }

    private void parseResponse(Response resp) throws Exception {
        // Read the response into an InputStream
        InputStream entityInputStream = resp.readEntity(InputStream.class);
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        // This system property is being set in the liberty-starter-application/build.gradle file
        String tempDir = System.getProperty("liberty.temp.dir");
        File file = new File(tempDir + "/TestApp.zip");
        System.out.println("Creating zip file: " + file.toString());
        file.getParentFile().mkdirs();
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));
        ZipEntry inputEntry = null;
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            String entryName = inputEntry.getName();
            zipOut.putNextEntry(new ZipEntry(entryName));
            if ("pom.xml".equals(entryName)) {
                System.out.println("Found pom.xml file.");
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = domFactory.newDocumentBuilder();
                Document doc = db.parse(zipIn);
                parseDependencies(doc.getElementsByTagName("dependencies"));
                parseRepositories(doc.getElementsByTagName("repository"));
                break;
            } else {
                byte[] bytes = new byte[4096];
                int bytesRead = 0;
                while ((bytesRead = zipIn.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, bytesRead);
                }
            }
            zipOut.closeEntry();
            zipIn.closeEntry();
        }
        zipOut.flush();
        zipIn.close();
        zipOut.close();
        System.out.println("Deleting file:" + file.toPath());
        Files.delete(file.toPath());
    }

    private void parseDependencies(NodeList pomDepend) {
        int size = pomDepend.getLength();
        System.out.println("Dependencies size is " + size);
        for (int i = 0; i < size; i++) {
            Element element = (Element) pomDepend.item(i);
            NodeList children = element.getElementsByTagName("dependency");
            for (int j = 0; j < children.getLength(); j++) {
                Element childElement = (Element) children.item(j);
                dependencySize = children.getLength();
                NodeList group = childElement.getElementsByTagName("groupId");
                for (int k = 0; k < group.getLength(); k++) {
                    Element groupEl = (Element) group.item(k);
                    String groupId = groupEl.getTextContent();
                    System.out.println("Groups found " + groupId);
                    groups = groups + groupId + ";";
                }
                NodeList artifact = childElement.getElementsByTagName("artifactId");
                for (int k = 0; k < artifact.getLength(); k++) {
                    Element artifactEl = (Element) artifact.item(k);
                    String artifactId = artifactEl.getTextContent();
                    System.out.println("Artifact found " + artifactId);
                    artifacts = artifacts + artifactId + ";";
                }
            }
        }
    }

    private void parseRepositories(NodeList repos) {
        int length = repos.getLength();
        for (int i = 0; i < length; i++) {
            Element repoNode = (Element) repos.item(i);
            Node urlNode = repoNode.getElementsByTagName("url").item(0);
            repoUrls = repoUrls + urlNode.getTextContent() + ";";
        }
    }
}
