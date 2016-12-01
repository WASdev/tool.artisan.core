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

import static com.ibm.liberty.starter.it.api.v1.matchers.FileContainsLines.containsLinesInRelativeOrder;
import static com.ibm.liberty.starter.it.api.v1.matchers.Retry.eventually;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RunLocalMavenPayloadTest {

    private final static String tempDir = System.getProperty("liberty.temp.dir") + "/localPayloadTest";
    private final static String installLog = tempDir + "/mvnLog/log.txt";
    private final static File stopServerLog = new File(tempDir + "/mvnLog/stopServer.txt");

    @Rule
    public DownloadedZip zip = new DownloadedZip(tempDir);

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
        
        assertEquals(0, mvnReturnCode);
        assertThat(stopServerLog, containsLinesInRelativeOrder(containsString("BUILD SUCCESS")));
    }

    private void runMvnInstallLibertyRunOnSeperateThread(File logFile) throws FileNotFoundException {
        PrintStream outputStream = MvnUtils.printStreamForFile(logFile);
        System.out.println("mvn output will go to " + logFile.getAbsolutePath());
        
        Thread threadExecutingInstall = new Thread(() -> {
            MvnUtils.runMvnCommand(outputStream, tempDir, zip, "install", "liberty:run-server", "-Daccept.features.license=true");
        });
        
        threadExecutingInstall.setDaemon(true);
        threadExecutingInstall.start();
    }
    
}
