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

import com.ibm.liberty.starter.it.api.v1.utils.DownloadedZip;
import com.ibm.liberty.starter.it.api.v1.utils.MvnUtils;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import static com.ibm.liberty.starter.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static com.ibm.liberty.starter.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static com.ibm.liberty.starter.matchers.Retry.eventually;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RunLocalMavenPayloadUsingBetaTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir") + "/localPayloadTestBeta";
    private final static String installLog = tempDir + "/mvnLog/logBeta.txt";
    private final static File stopServerLog = new File(tempDir + "/mvnLog/stopServerBeta.txt");

    @Rule
    public DownloadedZip zip = new DownloadedZip(tempDir, "beta=true");

    @Test
    public void testLocalMvnInstallRuns() throws Exception {
        File logFile = new File(installLog);
        String pathToOutputZip = zip.getLocation() + "/target/TestApp.zip";
        runMvnInstallLibertyRunOnSeperateThread(logFile);
        
        Matcher<File> logContainsServerStartedForLocalServer = containsLinesInRelativeOrder(containsString("run-server"), containsString("CWWKF0011I"));
        Matcher<File> logContainsBuildFailure = containsLinesInRelativeOrder(containsString("BUILD FAILURE"));
        assertThat(logFile, eventually(logContainsServerStartedForLocalServer).butNot(logContainsBuildFailure));
        assertThat(new File(pathToOutputZip), is(anExistingFile()));
        DownloadedZip.testEndpointOnRunningApplication();
    }

    @After
    public void stopServer() throws FileNotFoundException {
        PrintStream outputStream = MvnUtils.printStreamForFile(stopServerLog);
        
        int mvnReturnCode = MvnUtils.runMvnCommand(outputStream, tempDir, zip, "liberty:stop-server");
        
        assertEquals("mvn liberty:stop-server failed with non zero return code", 0, mvnReturnCode);
        assertThat(stopServerLog, containsLinesInRelativeOrder(containsString("BUILD SUCCESS")));
    }

    private void runMvnInstallLibertyRunOnSeperateThread(File logFile) throws FileNotFoundException {
        PrintStream outputStream = MvnUtils.printStreamForFile(logFile);
        System.out.println("mvn output will go to " + logFile.getAbsolutePath());
        
        Thread threadExecutingInstall = new Thread(() -> {
            MvnUtils.runMvnCommand(outputStream, tempDir, zip, "install", "liberty:run-server");
        });
        
        threadExecutingInstall.setDaemon(true);
        threadExecutingInstall.start();
    }
    
}