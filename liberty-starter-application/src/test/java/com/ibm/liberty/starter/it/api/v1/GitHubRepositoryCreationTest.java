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
package com.ibm.liberty.starter.it.api.v1;

import com.ibm.liberty.starter.*;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.registration.Service;
import com.ibm.liberty.starter.unit.MockServiceConnector;
import com.ibm.liberty.starter.unit.SetupInitialContext;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * <p>This class will test creating a project on GitHub. It will only run if you supply it your GitHub
 * OAuth token with the -DgitHubOauth=&lt;TOKEN&gt; flag when running this test. You can create a token by going
 * <a href="https://github.com/settings/tokens">here</a>. The token has to have the scope <code>public_repo</code>. You
 * can't already have a repository in GitHub called <code>TestAppAcceleratorProject</code>.</p>
 * <p>As the flow through the actual endpoint now has an OAuth flow enabled this doesn't actually go through the
 * endpoint. I think to do this we'll need some form of Selenium UI testing.</p>
 */
public class GitHubRepositoryCreationTest {

    @ClassRule
    public static SetupInitialContext setupInitialContext = new SetupInitialContext(Collections.singletonMap("serverOutputDir", "foo"));

    @Test
    public void testGitHubRepositoryCreation() throws Exception {
        String oAuthToken = System.getProperty("gitHubOauth");
        assumeTrue(oAuthToken != null && !oAuthToken.isEmpty());
        String name = "TestAppAcceleratorProject";
        String port = System.getProperty("liberty.test.port");
        URI baseUri = new URI("http://localhost:" + port + "/start");
        ServiceConnector serviceConnector = new MockServiceConnector(baseUri, new Dependency[]{});
        Services services = new Services();
        Service service = new Service();
        service.setId("wibble");
        List<Service> serviceList = Collections.singletonList(service);
        services.setServices(serviceList);
        ProjectConstructionInputData inputData = new ProjectConstructionInputData(services, serviceConnector, name, ProjectConstructor.DeployType.LOCAL, ProjectConstructor.BuildType.MAVEN, null, null, null, false);

        ProjectConstructor constructor = new ProjectConstructor(inputData);
        GitHubConnector connector = new GitHubConnector(oAuthToken);
        GitHubWriter writer = new GitHubWriter(constructor.buildFileMap(), inputData.appName, inputData.buildType, baseUri, connector);
        writer.createProjectOnGitHub();

        RepositoryService repositoryService = new RepositoryService();
        repositoryService.getClient().setOAuth2Token(oAuthToken);
        UserService userService = new UserService();
        userService.getClient().setOAuth2Token(oAuthToken);
        ContentsService contentsService = new ContentsService();
        contentsService.getClient().setOAuth2Token(oAuthToken);
        Repository repository = repositoryService.getRepository(userService.getUser().getLogin(), name);
        checkFileExists(contentsService, repository, "pom.xml");
        checkFileExists(contentsService, repository, "README.md");
    }

    private void checkFileExists(ContentsService contentsService, Repository repository, String path) throws IOException {
        List<RepositoryContents> file = contentsService.getContents(repository, path);
        assertThat(file, hasSize(1));
        assertThat(file.get(0).getSize(), greaterThan(0L));
    }

}