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
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.liberty.starter.build.maven.DomUtil;
import com.ibm.liberty.starter.it.api.v1.utils.DownloadZip;

public class MavenPomPayloadTest {

    private int dependencySize = 0;
    private String artifacts = "";
    private String groups = "";
    private String repoUrls = "";
    private String artifactId = "";
    private String groupId = "";
    private int responseStatus;
    private Object contentDisposition;
    private String configuration = "";

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
        String queryString = "tech=test&name=TestApp&deploy=local&artifactId=testArtifactId&groupId=test.group.id";
        String expectedUrl = "http://localhost:" + System.getProperty("liberty.test.port") + "/start/api/v1/repo";
        callDataEndpoint(queryString);
        assertTrue("Expected repo url to be:" + expectedUrl + ", instead found:" + repoUrls, repoUrls.contains(expectedUrl));
    }
    
    @Test
    public void testArtifactId() throws Exception {
        String queryString = "tech=test&name=Test&artifactId=test.artifact.id&deploy=local";
        callDataEndpoint(queryString);
        assertTrue("Expected test.artifact.id artifactId. Found " + artifactId, "test.artifact.id".equals(artifactId));
    }
    
    @Test
    public void testGroupId() throws Exception {
        String queryString = "tech=test&name=Test&groupId=test.group.id&deploy=local";
        callDataEndpoint(queryString);
        assertTrue("Expected test.group.id groupId. Found " + groupId, "test.group.id".equals(groupId));
    }
    
    @Test
    public void testBetaFlagInserted() throws Exception {
        String queryString = "tech=test&name=Test&groupId=test.group.id&deploy=local&beta=true";
        callDataEndpoint(queryString);
        assertTrue("Expected beta image. Found " + configuration, configuration.contains("webProfile7"));
        //assertTrue("Expected beta image. Found " + configuration, configuration.contains("beta"));
    }
    
    @Test
    public void testBetaFlagNotInserted() throws Exception {
        String queryString = "tech=test&name=Test&groupId=test.group.id&deploy=local";
        callDataEndpoint(queryString);
        assertTrue("Expected 17.0.0.1 image. Found " + configuration, configuration.contains("17.0.0.1"));
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


    private void callDataEndpoint(String queryString) throws Exception {
        Response response = DownloadZip.get(queryString);
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
        ZipEntry inputEntry = null;
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            String entryName = inputEntry.getName();
            if ("pom.xml".equals(entryName)) {
                System.out.println("Found pom.xml file.");
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = domFactory.newDocumentBuilder();
                Document doc = db.parse(zipIn);
                printPomToSysOut(doc);
                parsePomConfig(doc);
                parseDependencies(doc.getElementsByTagName("dependencies"));
                parseRepositories(doc.getElementsByTagName("repository"));
                parsePlugin(doc.getElementsByTagName("plugin"), "liberty-maven-plugin");
                break;
            }
            zipIn.closeEntry();
        }
        zipIn.close();
    }
    
    private void printPomToSysOut(Document pom) throws TransformerFactoryConfigurationError, TransformerException {
        DOMSource domSource = new DOMSource(pom);
        StreamResult streamResult = new StreamResult(System.out);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(domSource, streamResult);
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
    
    private void parsePomConfig(Document doc) {
        Node project = doc.getElementsByTagName("project").item(0);
        Node artifactIdNode = DomUtil.getChildNode(project, "artifactId", null);
        artifactId = artifactIdNode.getTextContent();
        Node groupIdNode = DomUtil.getChildNode(project, "groupId", null);
        groupId = groupIdNode.getTextContent();
    }
    
    private void parsePlugin(NodeList pluginNodeList, String artifactId) {
        for (int i = 0; i < pluginNodeList.getLength(); i++) {
        	Element pluginNode = (Element) pluginNodeList.item(i);
            if (DomUtil.nodeHasId(pluginNode, artifactId)) {
            	NodeList configNodeList = pluginNode.getElementsByTagName("configuration");
            	Node configNode = configNodeList.item(0);
            	if (configNode != null) {
            		configuration = configNode.getTextContent();
            		System.out.println("Configuration is " + configuration);
            	}
            }
        }
    }
}
