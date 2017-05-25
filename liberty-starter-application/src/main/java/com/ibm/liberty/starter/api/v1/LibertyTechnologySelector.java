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

import com.ibm.liberty.starter.*;
import org.xml.sax.SAXException;

import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Logger;

@Path("v1/data")
public class LibertyTechnologySelector {

    private static final Logger log = Logger.getLogger(LibertyTechnologySelector.class.getName());

    @GET
    @Produces("application/zip")
    public Response getResponse(@QueryParam("tech") String[] techs, @QueryParam("techoptions") String[] techOptions, @QueryParam("name") String name,
                                @QueryParam("deploy") final String deploy, @QueryParam("workspace") final String workspaceId, @QueryParam("build") final String build, 
                                @QueryParam("artifactId") String artifactId, @QueryParam("groupId") String groupId, @Context UriInfo info,
                                @QueryParam("beta") boolean beta) throws NullPointerException, IOException {
        log.info("GET request for /data");
        try {
            ProjectConstructionInput inputProcessor = new ProjectConstructionInput(new ServiceConnector(info.getBaseUri()));
            final ProjectConstructionInputData inputData = inputProcessor.processInput(techs, techOptions, name, deploy, workspaceId, build, artifactId, groupId, beta);
            StreamingOutput so = (OutputStream os) -> {
                ProjectConstructor projectConstructor = new ProjectConstructor(inputData);
                try {
                    Map<String, byte[]> fileMap = projectConstructor.buildFileMap();
                    ZipWriter zipConstructor = new ZipWriter(fileMap);
                    zipConstructor.buildZip(os);
                } catch (SAXException | ParserConfigurationException | TransformerException e) {
                    throw new WebApplicationException(e);
                }
            };
            name += ".zip";
            return Response.ok(so, "application/zip").header("Content-Disposition", "attachment; filename=\"" + name + "\"").build();
        } catch (ValidationException e) {
            return Response.status(Status.BAD_REQUEST).entity("Validation of the input failed.").build();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.severe(e.getClass().getName() + " caught " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
