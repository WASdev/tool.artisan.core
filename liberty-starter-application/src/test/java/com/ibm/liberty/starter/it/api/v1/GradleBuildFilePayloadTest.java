/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
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

import static com.ibm.liberty.starter.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.liberty.starter.it.api.v1.utils.DownloadZip;

public class GradleBuildFilePayloadTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir") + "/GradleBuildFilePayloadTest";
    private final static String buildFilePath = tempDir + "/build.gradle";
    private final static String settingsFilePath = tempDir + "/settings.gradle";
    private int responseStatus;

    @Test
    public void dependenciesInserted() throws Exception {
        String queryString = "tech=test&name=TestApp&deploy=local&build=gradle";
        callDataEndpoint(queryString, ".dependenciesInserted");
        File buildFile = new File(buildFilePath + ".dependenciesInserted");
        assertThat(buildFile, containsLinesInRelativeOrder(containsString("providedCompile 'net.wasdev.wlp.starters.test:provided-pom:0.0.1'")));
        assertThat(buildFile, containsLinesInRelativeOrder(containsString("runtime 'net.wasdev.wlp.starters.test:runtime-pom:0.0.1'")));
        assertThat(buildFile, containsLinesInRelativeOrder(containsString("compile 'net.wasdev.wlp.starters.test:compile-pom:0.0.1'")));
    }
    
    @Test
    public void repoUrlInserted() throws Exception {
        File buildFile = new File(buildFilePath + ".repoUrlInserted");
        String queryString = "tech=test&name=TestApp&deploy=local&build=gradle";
        String expectedUrl = "url 'http://localhost:" + System.getProperty("liberty.test.port") + "/start/api/v1/repo'";
        callDataEndpoint(queryString, ".repoUrlInserted");
        assertThat(buildFile, containsLinesInRelativeOrder(containsString(expectedUrl)));
    }
    
    @Test
    public void artifactIdInserted() throws Exception {
        File buildFile = new File(settingsFilePath + ".artifactIdInserted");
        String queryString = "tech=test&name=TestApp&artifactId=testArtifactId&deploy=local&build=gradle";
        callDataEndpoint(queryString, ".artifactIdInserted");
        assertThat(buildFile, containsLinesInRelativeOrder(containsString("rootProject.name = 'testArtifactId'")));
    }
    
    @Test
    public void groupIdInserted() throws Exception {
        File buildFile = new File(buildFilePath + ".groupIdInserted");
        String queryString = "tech=test&name=TestApp&groupId=test.group.id&deploy=local&build=gradle";
        callDataEndpoint(queryString, ".groupIdInserted");
        assertThat(buildFile, containsLinesInRelativeOrder(containsString("group = 'test.group.id'")));
    }
     
    @Test
    public void betaFlagInserted() throws Exception {
        File buildFile = new File(buildFilePath + ".betaFlagInserted");
        String queryString = "tech=test&name=TestApp&deploy=local&build=gradle&beta=true";
        callDataEndpoint(queryString, ".betaFlagInserted");
        assertThat(buildFile, containsLinesInRelativeOrder(containsString("wlp-beta")));
    }

    @Test
    public void betaFlagNotInserted() throws Exception {
        File buildFile = new File(buildFilePath + ".betaFlagNotInserted");
        String queryString = "tech=test&name=TestApp&deploy=local&build=gradle";
        callDataEndpoint(queryString, ".betaFlagNotInserted");
        assertThat(buildFile, containsLinesInRelativeOrder(containsString("wlp-webProfile7")));
    }
    
    private void callDataEndpoint(String queryString, String fileEnding) throws Exception {
        Response response = DownloadZip.get(queryString);
        try {
            responseStatus = response.getStatus();
            if (this.responseStatus != 200) {
                Assert.fail("Expected response status 200, instead found " + responseStatus + ". Response was " + response.readEntity(String.class));
            }
            parseResponse(response, fileEnding);
        } finally {
            response.close();
        }
    }
    
    private void parseResponse(Response response, String fileEnding) throws IOException {
        // Read the response into an InputStream
        InputStream entityInputStream = response.readEntity(InputStream.class);
        // Create a new ZipInputStream from the response InputStream
        ZipInputStream zipIn = new ZipInputStream(entityInputStream);
        // This system property is being set in the liberty-starter-application/build.gradle file
        File file = new File(tempDir + "/TestApp.zip");
        System.out.println("Creating zip file: " + file.toString());
        file.getParentFile().mkdirs();
        ZipEntry inputEntry = null;
        while ((inputEntry = zipIn.getNextEntry()) != null) {
            String entryName = inputEntry.getName();
            if ("build.gradle".equals(entryName)) {
                writeEntryToFile(buildFilePath + fileEnding, zipIn);
            }
            if ("settings.gradle".equals(entryName)) {
                writeEntryToFile(settingsFilePath + fileEnding, zipIn);
            }
        }
        zipIn.close();
        entityInputStream.close();
    }
    
    private void writeEntryToFile(String filePath, ZipInputStream zipIn) throws IOException {
        System.out.println("Creating " + filePath);
        File zipFile = new File(filePath);
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
