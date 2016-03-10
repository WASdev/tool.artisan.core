package com.ibm.liberty.starter.it.api.v1;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class RunLocalPayloadTest {
    
    private final String extractedZip = "/extractedZip";
    private final String installLog = "/mvnLog/log.txt";
    private final String cleanLog = "/mvnLog/cleanLog.txt";

//    @BeforeClass
//     check that maven is on the classpath
//     public static void assertMavenPathSet() {
//      System.getenv(")
//    }


    @Before
    public void downloadZip() throws IOException {
        Client client = ClientBuilder.newClient();
        String port = System.getProperty("liberty.test.port");
        String url = "http://localhost:" + port + "/start/api/v1/data?tech=test&name=TestApp&deploy=local";
        System.out.println("Testing " + url);
        Response response = client.target(url).request("application/zip").get();
        InputStream entityInputStream = response.readEntity(InputStream.class);
        extractZip(entityInputStream);
    }
    
    @Test
    public void testLocalMvnInstallRuns() throws Exception {
        testMvnInstall();
        testEndpoint();
        testMvnClean();
    }
    
    public void testMvnInstall() throws IOException, InterruptedException {
        
        runMvnInstall();
    }
    
    public void testEndpoint() {
        Client client = ClientBuilder.newClient();
        String url = "http://localhost:9080/myLibertyApp/";
        System.out.println("Testing " + url);
        Response response = client.target(url).request().get();
        int status = response.getStatus();
        assertTrue("Response status was not 200, found:" + status, status == 200);
        String responseString = response.readEntity(String.class);
        String[] expectedStrings = {"Welcome to your Liberty Application", "Test"};
        assertTrue("Response incorrect, expected:" + expectedStrings[0] + ", found:" + responseString, responseString.contains(expectedStrings[0]));
        assertTrue("Response incorrect, expected:" + expectedStrings[1] + ", found:" + responseString, responseString.contains(expectedStrings[1]));
    }
    
    public void testMvnClean() throws IOException, InterruptedException {
        runMvnClean();
    }

    private static void extractZip(InputStream entityInputStream) throws IOException {
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        String tempDir = System.getProperty("liberty.temp.dir");
        File file = new File(tempDir + "/TestApp.zip");
        System.out.println("Creating zip file: " + file.toString());
        File extractedZip = new File(tempDir + "/extractedZip");
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
            };
            fos.close();
        }
    }

    private File runMvnInstall() throws IOException, InterruptedException {
        String filePath = System.getProperty("liberty.temp.dir") + extractedZip;
        String logFilePath = System.getProperty("liberty.temp.dir") + installLog;
        File logFile = new File(logFilePath);
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
        System.out.println("mvn output will go to " + logFilePath);
        File file = new File(filePath);
        Process process = Runtime.getRuntime().exec("cmd /c mvn install --log-file " + logFilePath, null, file);
        process.waitFor();
        int exitValue = process.exitValue();
        System.out.println("Exit value is " + exitValue);
        assertTrue("Expected return value of 0, instead found:" + exitValue, exitValue == 0);
        return logFile;
    }
    
    private File runMvnClean() throws IOException, InterruptedException {
        String filePath = System.getProperty("liberty.temp.dir") + extractedZip;
        String logFilePath = System.getProperty("liberty.temp.dir") + cleanLog;
        File logFile = new File(logFilePath);
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
        System.out.println("mvn output will go to " + logFilePath);
        File file = new File(filePath);
        Process process = Runtime.getRuntime().exec("cmd /c mvn clean -P stopServer --log-file " + logFilePath, null, file);
        process.waitFor();
        int exitValue = process.exitValue();
        System.out.println("Exit value is " + exitValue);
        assertTrue("Expected return value of 0, instead found:" + exitValue, exitValue == 0);
        return logFile;
    }

}
