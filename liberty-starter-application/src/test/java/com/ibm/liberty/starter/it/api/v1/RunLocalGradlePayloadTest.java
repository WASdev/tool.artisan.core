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

import static com.ibm.liberty.starter.it.api.v1.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RunLocalGradlePayloadTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir") + "/localGradlePayloadTest";
    private final static String installLog = tempDir + "/gradleLog/log.txt";
    private final static File stopServerLog = new File(tempDir + "/gradleLog/stopServer.txt");

    @Rule
    public DownloadedZip zip = new DownloadedZip(tempDir, "build=gradle");

    @Test
    public void testLocalGradleInstallRuns() throws Exception {
        File logFile = new File(installLog);
        String pathToOutputZip = zip.getLocation() + "/build/TestApp.zip";

        int returnCode = runGradleCommand(logFile, "build", "libertyStart");

        assertEquals(0, returnCode);
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
        commands.add("-PacceptFeaturesLicense=true");
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
