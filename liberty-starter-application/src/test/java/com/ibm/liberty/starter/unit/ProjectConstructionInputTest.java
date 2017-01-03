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
package com.ibm.liberty.starter.unit;

import com.ibm.liberty.starter.ProjectConstructionInput;
import com.ibm.liberty.starter.ProjectConstructionInputData;
import com.ibm.liberty.starter.ProjectConstructor;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.naming.NamingException;
import javax.validation.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class ProjectConstructionInputTest {

    private ProjectConstructionInput testObject;
    private MockServiceConnector serviceConnector;

    @Before
    public void createTestObject() throws URISyntaxException {
        serviceConnector = new MockServiceConnector(new URI(""), null);
        testObject = new ProjectConstructionInput(serviceConnector);
    }

    @ClassRule
    public static SetupInitialConext setupInitialConext = new SetupInitialConext();

    @Test
    public void validInputIsParsedIntoDataObject() throws URISyntaxException, NamingException {
        String techName = "wibble";
        String techOption = "fish";
        String name = "testName";
        String workspaceId = "randomId";
        String artifactId = "testArtifactId";
        String groupId = "test.group.id";

        ProjectConstructionInputData result = testObject.processInput(new String[] {techName}, new String[] {techName + ":" + techOption}, name, "local", workspaceId, "gradle", artifactId, groupId);

        assertThat(result.appName, is(name));
        assertThat(result.buildType, is(ProjectConstructor.BuildType.GRADLE));
        assertThat(result.deployType, is(ProjectConstructor.DeployType.LOCAL));
        assertThat(result.workspaceDirectory, containsString(workspaceId));
        assertThat(result.serviceConnector, is(serviceConnector));
        assertThat(result.services.getServices(), hasSize(1));
        assertThat(result.services.getServices().get(0).getId(), is(techName));
        assertThat(result.artifactId, is(artifactId));
        assertThat(result.groupId, is(groupId));
        assertThat(serviceConnector.capturedTechWorkspaceDir, containsString(workspaceId));
        assertThat(serviceConnector.capturedTechs, is(new String[] {techName}));
        assertThat(serviceConnector.capturedOptions, is(techOption));
    }

    @Test(expected = ValidationException.class)
    public void noNameThrowsAValidationException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, null, "local", "wibble", "gradle", null, null);
    }

    @Test(expected = ValidationException.class)
    public void nameWithInvalidCharactersThrowsAValidationException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "wibble%", "local", "wibble", "gradle", null, null);
    }

    @Test(expected = ValidationException.class)
    public void longNameThrowsAValidationException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "ThisIsAReallyLongNameButIsItLongEnoughNotQuiteSoLetsKeepGoing", "local", "wibble", "gradle", null, null);
    }

    @Test(expected = ValidationException.class)
    public void noDeployThrowsAValidationException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "wibble", null, "wibble", "gradle", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidDeployThrowsAIllegalArgumentException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "wibble", "invalid", "wibble", "gradle", null, null);
    }

    @Test
    public void noBuildDefaultsToMaven() throws Exception {
        ProjectConstructionInputData result = testObject.processInput(new String[]{}, new String[]{}, "wibble", "local", "wibble", null, null, null);

        assertThat(result.buildType, is(ProjectConstructor.BuildType.MAVEN));
    }

    @Test
    public void invalidBuildDefaultsToMaven() throws Exception {
        ProjectConstructionInputData result = testObject.processInput(new String[]{}, new String[]{}, "wibble", "local", "wibble", "wibble", null, null);

        assertThat(result.buildType, is(ProjectConstructor.BuildType.MAVEN));
    }
    
    @Test(expected = ValidationException.class)
    public void invalidArtifactIdThrowsIllegalArgumentException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "wibble", "local", "wibble", "gradle", "%wibble", null);
    }
    
    @Test(expected = ValidationException.class)
    public void invalidGroupIdThrowsIllegalArgumentException() throws Exception {
        testObject.processInput(new String[] {}, new String[] {}, "wibble", "local", "wibble", "gradle", null, "%wibble");
    }
}
