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
package com.ibm.liberty.starter.api.v1;

import com.ibm.liberty.starter.*;

import javax.validation.ValidationException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Path("v1/createGitHubRepository")
public class GitHubProjectEndpoint {

    private static final Logger log = Logger.getLogger(GitHubProjectEndpoint.class.getName());

    @GET
    public Response getResponse(@QueryParam("tech") String[] techs, @QueryParam("techoptions") String[] techOptions, @QueryParam("name") String name,
                                @QueryParam("deploy") String deploy, @QueryParam("workspace") String workspaceId, @QueryParam("build") String build,
                                @QueryParam("artifactId") String artifactId, @QueryParam("groupId") String groupId,
                                @QueryParam("oAuthToken") String oAuthToken, @Context UriInfo info) throws NullPointerException, IOException {
        log.info("GET request for v1/createGitHubRepository");
        try {
            if (oAuthToken == null || oAuthToken.length() == 0) {
                log.severe("No oAuthToken passed in.");
                throw new ValidationException();
            }
            oAuthToken = URLEncoder.encode(oAuthToken, StandardCharsets.UTF_8.name());
            URI baseUri = info.getBaseUri();
            ProjectConstructionInput inputProcessor = new ProjectConstructionInput(new ServiceConnector(baseUri));
            ProjectConstructionInputData inputData = inputProcessor.processInput(techs, techOptions, name, deploy, workspaceId, build, artifactId, groupId);
            ProjectConstructor constructor = new ProjectConstructor(inputData);
            GitHubConnector connector = new GitHubConnector(oAuthToken);
            GitHubWriter writer = new GitHubWriter(constructor.buildFileMap(), inputData.appName, inputData.buildType, baseUri, connector);
            writer.createProjectOnGitHub();
            return Response.seeOther(new URI(connector.getRepositoryLocation())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.FORBIDDEN).build();
        } catch (ValidationException e) {
            return Response.status(Status.BAD_REQUEST).entity("Validation of the input failed.").build();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.severe(e.getClass().getName() + " occurred processing request: " + e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
