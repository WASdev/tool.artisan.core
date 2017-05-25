/*******************************************************************************
 * Copyright (c) 2016, 2017 IBM Corp.
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

import com.ibm.liberty.starter.ProjectConstructionInput;
import com.ibm.liberty.starter.ServiceConnector;

import javax.naming.InitialContext;
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
import java.util.logging.Logger;

@Path("v1/createGitHubRepository")
public class GitHubProjectEndpoint {

    private static final Logger log = Logger.getLogger(GitHubProjectEndpoint.class.getName());

    @GET
    public Response getResponse(@QueryParam("tech") String[] techs, @QueryParam("techoptions") String[] techOptions, @QueryParam("name") String name,
                                @QueryParam("deploy") String deploy, @QueryParam("workspace") String workspaceId, @QueryParam("build") String build,
                                @QueryParam("artifactId") String artifactId, @QueryParam("groupId") String groupId, @Context UriInfo info,
                                @QueryParam("beta") boolean beta) throws NullPointerException, IOException {
        log.info("GET request for v1/createGitHubRepository");
        try {
            URI baseUri = info.getBaseUri();
            ProjectConstructionInput inputProcessor = new ProjectConstructionInput(new ServiceConnector(baseUri));

            // Use a JWT as the "state" object on the GitHub OAuth API. This object allows us to check the validity of
            // the callback when it comes back. By using a signed JWT we can both store all of the parameters in the
            // JWT and have an integrity check. Using a JWT means this is all done for us through a 3rd party library
            // rather than having to encode/decode it ourselves as well as providing checks on expiry times. Also a
            // JWT means that the callback becomes stateless rather than having to store the state object in the user's
            // session (although the workspace dir contains some state).
            String state = inputProcessor.processInputAsJwt(techs, techOptions, name, deploy, workspaceId, build, artifactId, groupId, beta);
            String clientId = (String) new InitialContext().lookup("gitHubClientId");
            URI gitHubAuth = new URI("https://github.com/login/oauth/authorize?client_id=" + clientId + "&scope=public_repo&state=" + state);
            log.info("redirecting to " + gitHubAuth);
            return Response.seeOther(gitHubAuth).build();
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
