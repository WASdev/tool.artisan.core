package com.ibm.liberty.starter.it.api.v1;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ibm.liberty.starter.it.api.v1.utils.DownloadZip;

public class PayloadStructureTest {
    private int responseStatus;
    
    @Test
    public void testBase() throws Exception {
        String queryString = "tech=test&name=Test&deploy=local";
        Response response = DownloadZip.get(queryString);
        try {
            responseStatus = response.getStatus();
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
}
