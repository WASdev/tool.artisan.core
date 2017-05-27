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
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ibm.liberty.starter.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RunLocalGradlePayloadUsingBetaTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir") + "/localGradlePayloadTestBeta";
    private final static String installLog = tempDir + "/gradleLog/logBeta.txt";
    private final static File stopServerLog = new File(tempDir + "/gradleLog/stopServerBeta.txt");

    @Rule
    public DownloadedZip zip = new DownloadedZip(tempDir, "build=gradle", "beta=true");

    @Test
    public void testLocalGradleInstallRuns() throws Exception {
        File logFile = new File(installLog);
        String pathToOutputZip = zip.getLocation() + "/build/TestApp.zip";

        int returnCode = runGradleCommand(logFile, "build", "libertyStart");

        assertEquals("gradle build libertyStart failed with non zero return code", 0, returnCode);
        assertThat(logFile, containsLinesInRelativeOrder(containsString("BUILD SUCCESSFUL")));
        assertThat(new File(pathToOutputZip), is(anExistingFile()));
        DownloadedZip.testEndpointOnRunningApplication();
    }

    @After
    public void stopServer() throws IOException, InterruptedException {
        int returnCode = runGradleCommand(stopServerLog, "libertyStop");
        
        assertEquals(0, returnCode);
        assertThat(stopServerLog, containsLinesInRelativeOrder(containsString("BUILD SUCCESSFUL")));
    }

    private int runGradleCommand(File logFile, String... gradleTasks) throws InterruptedException, IOException {
        System.out.println("Running command for " + gradleTasks);
        List<String> commands = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();
        String gradleCommand = System.getProperty("gradleCommand", "gradle");
        System.out.println("gradleCommand = " + gradleCommand);
        if (os.contains("windows")) {
            commands.add("cmd");
            commands.add("/c");
        }
        commands.add(gradleCommand);
        commands.addAll(Arrays.asList(gradleTasks));
        logFile.getParentFile().mkdir();
        System.out.println("commands = " + commands);
        final Process gradleProcess = new ProcessBuilder(commands)
                .directory(new File(zip.getLocation()))
                .redirectErrorStream(true)
                .redirectOutput(logFile)
                .start();
        return gradleProcess.waitFor();
    }
}
