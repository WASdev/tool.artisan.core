package com.ibm.liberty.starter.it.api.v1.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.rules.ExternalResource;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * A JUnit {@link Rule} that downloads the payload from the URL <code>http://localhost:${liberty.test.port}/start/api/v1/data?tech=test&name=TestApp&deploy=local</code>. It then
 * extracts it into the location supplied by {@link #getLocation()}.
 */
public class DownloadedZip extends ExternalResource {

    private final File extractedZip;
    private String[] args;

    public DownloadedZip(String tempDir, String... args) {
        this.extractedZip = new File(tempDir + "/extractedZip");
        this.args = args;
    }

    @Override
    protected void before() throws Throwable {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String additionalArgs = "";
        if (args != null && args.length > 0) {
            additionalArgs = "&" + Arrays.stream(args).collect(Collectors.joining("&"));
        }
        String url = "http://localhost:" + port + "/start/api/v1/data?tech=test&name=TestApp&deploy=local" + additionalArgs;
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        InputStream entityInputStream = response.readEntity(InputStream.class);
        extractZip(entityInputStream);
        entityInputStream.close();
        response.close();
    }

    private void extractZip(InputStream entityInputStream) throws IOException {
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        ZipEntry inputEntry = null;
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            if (inputEntry.isDirectory()) {
                continue;
            }
            String entryName = inputEntry.getName();
            System.out.println("Creating " + entryName);
            File zipFile = new File(extractedZip, entryName);
            zipFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(zipFile);
            byte[] bytes = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = zipIn.read(bytes)) >= 0) {
                fos.write(bytes, 0, bytesRead);
            } ;
            fos.close();
        }
    }

    public String getLocation() {
        return extractedZip.getAbsolutePath();
    }

    public static void testEndpointOnRunningApplication() {
        Client client = ClientBuilder.newClient();
        String url = "http://localhost:9080/myLibertyApp/";
        System.out.println("Testing " + url);
        WebTarget target = client.target(url);
        Invocation.Builder builder = target.request();
        Response response = builder.get();
        int status = response.getStatus();
        assertEquals("Endpoint response status was not 200, found:" + status, status, 200);
        String responseString = response.readEntity(String.class);
        assertThat(responseString, containsString("Welcome to your Liberty Application"));
        assertThat(responseString, containsString("Test"));
    }

}
