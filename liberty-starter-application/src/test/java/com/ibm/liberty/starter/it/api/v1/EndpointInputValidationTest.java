package com.ibm.liberty.starter.it.api.v1;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EndpointInputValidationTest {
    
    @Test
    public void testServiceFinderEndpoint() throws Exception {
        String endpoint = "/start/api/v1/services";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == 200);
    }

    @Test
    public void testTechFinderInvalidTechType() throws Exception {
        String endpoint = "/start/api/v1/tech/ABC123";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testTechSelectorInvalidTechType() throws Exception {
        String endpoint = "/start/api/v1/data?tech=ABC123&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testTechSelectorValidName() throws Exception {
        String endpoint = "/start/api/v1/data?tech=test&name=testName&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == 200);
    }

    @Test
    public void testTechSelectorInvalidName() throws Exception {
        String endpoint = "/start/api/v1/data?tech=test&name=in/valid&deploy=local";
        Response response = callEndpoint(endpoint);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRepoInvalidTech() throws Exception {
        String url = "/start/api/v1/repo/net/wasdev/wlp/starters/abc123/provided-pom/0.0.1/provided-pom-0.0.1.pom";
        Response response = callEndpoint(url);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testRepoInvalidPath() throws Exception {
        String url = "/start/api/v1/repo/net/wasdev/wlp/starters/test/&=";
        Response response = callEndpoint(url);
        int status = response.getStatus();
        response.close();
        assertTrue("Response incorrect, response status was " + status, status == Response.Status.BAD_REQUEST.getStatusCode());
    }

    private Response callEndpoint(String endpoint) throws Exception {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + endpoint;
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
        return response;
    }

    @Test
    public void testStarterWorkspaceEndpoint() throws Exception {
        String endpoint = "/start/api/v1/workspace";
        Response response = callEndpoint(endpoint);
        try {
            int status = response.getStatus();
        assertEquals("Response incorrect, response status was " + status, 200, status);
        String workspaceId = response.readEntity(String.class);
        assertNotNull("Returned workspace ID was not a valid UUID : " + workspaceId, UUID.fromString(workspaceId));
        } finally {
            response.close();
        }
    }
    
    @Test
    public void testUploadInvalidNoTech() throws Exception {
        int response = invokeUploadEndpoint("","sampleFileNoTech.txt");
        assertEquals("Response incorrect, response was " + response, Response.Status.BAD_REQUEST.getStatusCode(), response);
    }
    
    @Test
    public void testUploadInvalidNoWorkspace() throws Exception {
        int response = invokeUploadEndpoint("tech=test", "sampleFileNoWorkspace.txt");
        assertEquals("Response incorrect, response was " + response, Response.Status.BAD_REQUEST.getStatusCode(), response);
    }
    
    /**
     * **IMPORTANT** - Please ensure that this test always runs and passes as micro-services such as Swagger rely 
     * on the functionalities being tested by this test. 
     * 
     * Never ignore/skip this test. If it fails then investigate and fix the core so that the test passes. 
     */
    @Test
    public void testUploadProcessPackage() throws Exception {
    	String uuid = UUID.randomUUID().toString();
    	
    	// Upload a file
        int responseCode = invokeUploadEndpoint("tech=test&workspace=" + uuid, "sampleUpload.txt");
        assertEquals("Response 1 was incorrect, response was " + responseCode, Response.Status.OK.getStatusCode(), responseCode);
        
        // Upload second file (without cleaning up)
        responseCode = invokeUploadEndpoint("tech=test&workspace=" + uuid, "sampleUpload2.txt");
        assertEquals ("Response 2 was incorrect, response was " + responseCode, Response.Status.OK.getStatusCode(), responseCode);
        
        // Upload third file (after cleaning up existing files) and process the file
        responseCode = invokeUploadEndpoint("tech=test&workspace=" + uuid + "&cleanup=true&process=true", "sampleUpload3.txt");
        assertEquals("Response 3 was incorrect, response was " + responseCode, Response.Status.OK.getStatusCode(), responseCode);
        
        // Invoke the v1/data endpoint to ensure that the packaged files are contained within the zip and the features to 
        // install specified by the 'test' micro-service are present within pom.xml
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?tech=test&name=Test&deploy=local&techoptions=test:testoption1&workspace=" + uuid;
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        try {
            int responseStatus = response.getStatus();
            assertEquals("Incorrect response code. Response status is: " + responseStatus, 200, responseStatus);
            // Read the response into an InputStream
            InputStream entityInputStream = response.readEntity(InputStream.class);
            // Create a new ZipInputStream from the response InputStream
            ZipInputStream zipIn = new ZipInputStream(entityInputStream);
            ZipEntry inputEntry = null;
            boolean packagedFileExists = false;
            boolean deletedFileExists = false;
            boolean foundFeaturesToInstall = false;
            while ((inputEntry = zipIn.getNextEntry()) != null) {
                String entryName = inputEntry.getName();
                if ("sampleUpload3.txt_renamed".equals(entryName)) {
                    packagedFileExists = true;
                } else if ("sampleUpload.txt".equals(entryName) || "sampleUpload2.txt".equals(entryName) || "sampleUpload3.txt".equals(entryName)) {
                    deletedFileExists = true;
                } else if ("pom.xml".equals(entryName)) {
                    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = domFactory.newDocumentBuilder();
                    // Use an annonomous inner class to delegate to the zip input stream as db.parse closes the stream
                    Document doc = db.parse(new InputStream() {

                        @Override
                        public int read() throws IOException {
                            return zipIn.read();
                        }

                    });
                    Node assemblyInstallDirectory = doc.getElementsByTagName("assemblyInstallDirectory").item(0);
                    assertNotNull("assemblyInstallDirectory node was not found within pom.xml", assemblyInstallDirectory);
                    Node configuration = assemblyInstallDirectory.getParentNode();
                    Node features = getChildNode(configuration, "features");
                    assertNotNull("features node was not found within pom.xml", features);
                    assertTrue("Install feature was not found : servlet-3.1", hasChildNode(features, "feature", "servlet-3.1"));
                    assertTrue("Install feature was not found : apiDiscovery-1.0", hasChildNode(features, "feature", "apiDiscovery-1.0"));
                    assertTrue("acceptLicense node with ${accept.features.license} property was not found", hasChildNode(features, "acceptLicense", "${accept.features.license}"));

                    Node plugins = configuration.getParentNode().getParentNode();
                    Node artifactIdNode = getGrandchildNode(plugins, "plugin", "artifactId", "maven-enforcer-plugin");
                    assertNotNull("maven-enforcer-plugin was not found", artifactIdNode);
                    Node enforcerPlugin = artifactIdNode.getParentNode();
                    Node executions = getChildNode(enforcerPlugin, "executions");
                    assertNotNull("executions node was not found within maven-enforcer-plugin", executions);
                    Node enforcePropertyNode = getGrandchildNode(executions, "execution", "id", "enforce-property");
                    assertNotNull("enforce-property id was not found within maven-enforcer-plugin", enforcePropertyNode);
                    Node execution = enforcePropertyNode.getParentNode();
                    Node configurationNode = getChildNode(execution, "configuration");
                    assertNotNull("configuration node was not found within maven-enforcer-plugin", configurationNode);
                    Node rules = getChildNode(configurationNode, "rules");
                    assertNotNull("rules node was not found within maven-enforcer-plugin", rules);
                    Node acceptLicenseProperty = getGrandchildNode(rules, "requireProperty", "property", "accept.features.license");
                    assertNotNull("requireProperty with accept.features.license property was not found within maven-enforcer-plugin", acceptLicenseProperty);
                    foundFeaturesToInstall = true;
                }
                zipIn.closeEntry();
            }
            zipIn.close();

            assertTrue("Packaged file doesn't exist at sampleUpload3.txt_renamed in the zip file", packagedFileExists);
            assertTrue("Features to install were not found in pom.xml from the zip file", foundFeaturesToInstall);
            assertFalse("Deleted file exists in the zip file", deletedFileExists);
        } finally {
            response.close();
        }
    }
    
    private boolean hasChildNode(Node parentNode, String nodeName, String nodeValue){
    	return getChildNode(parentNode, nodeName, nodeValue) != null ? true : false;
    }
    
    /**
     * Get the matching grand child node
     * @param parentNode - the parent node
     * @param childNodeName - name of child node to match
     * @param grandChildNodeName - name of grand child node to match 
     * @param grandChildNodeValue - value of grand child node to match
     * @return the grand child node if a match was found, null otherwise
     */
    private Node getGrandchildNode(Node parentNode, String childNodeName, String grandChildNodeName, String grandChildNodeValue){
    	List<Node> matchingChildren = getChildren(parentNode, childNodeName, null);
    	for(Node child : matchingChildren){
    		Node matchingGrandChild = getChildNode(child, grandChildNodeName, grandChildNodeValue);
    		if(matchingGrandChild != null){
    			return matchingGrandChild;
    		}
    	}
    	return null;  	
    }
    
    /**
	 * Get all matching child nodes
	 * @param parentNode - the parent node
	 * @param name - name of child node to match 
	 * @param value - value of child node to match, specify null to not match value
	 * @return matching child nodes
	 */
	private static List<Node> getChildren(Node parentNode, String name, String value){
		List<Node> childNodes = new ArrayList<Node>();
		if(parentNode == null || name == null){
			return childNodes;
		}

		if (parentNode.getNodeType() == Node.ELEMENT_NODE && parentNode.hasChildNodes()) {
			NodeList children = parentNode.getChildNodes();
			for(int i=0; i < children.getLength(); i++){
				Node child = children.item(i);
				if(child != null && name.equals(child.getNodeName()) && (value == null || value.equals(child.getTextContent()))){
					childNodes.add(child);
				}
			}
		}

		return childNodes;
	}
	
    /**
     * Get the matching child node
     * @param parentNode - the parent node
     * @param name - name of child node to match 
     * @param value - value of child node to match
     * @return the child node if a match was found, null otherwise
     */
    private Node getChildNode(Node parentNode, String name, String value){
    	List<Node> matchingChildren = getChildren(parentNode, name, value);
    	return (matchingChildren.size() > 0) ? matchingChildren.get(0) : null;
    }
    
    private Node getChildNode(Node parentNode, String name){
    	return getChildNode(parentNode, name, null);
    }
    
    private int invokeUploadEndpoint(String params, String fileName) throws Exception {
        String port = System.getProperty("liberty.test.port");
        String path = "http://localhost:" + port + "/start/api/v1/upload" + ((params != null && !params.trim().isEmpty()) ? ("?" + params) : "");
        System.out.println("Testing " + path);
        
        String boundary = "----WebKitFormBoundarybcoFJqLu81T8NPk8";
        URL url = new URL(path);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setUseCaches(false);
        httpConnection.setDoInput(true);
        httpConnection.setDoOutput(true);        
        
        httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream outputStream = httpConnection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
        
        final String NEW_LINE = "\r\n";
        writer.append("--" + boundary).append(NEW_LINE);
        writer.append("Content-Disposition: form-data; name=\"fileFormData\"; filename=\"" + fileName + "\"").append(NEW_LINE);
        writer.append("Content-Type: application/octet-stream").append(NEW_LINE).append(NEW_LINE);
        writer.append("Content of the file line 1").append(NEW_LINE).append("Content of file line 2").append(NEW_LINE);
        writer.append(NEW_LINE).flush();
        writer.append("--" + boundary + "--").append(NEW_LINE);
        writer.close();
        
        httpConnection.disconnect();
        return httpConnection.getResponseCode();
    }

}
