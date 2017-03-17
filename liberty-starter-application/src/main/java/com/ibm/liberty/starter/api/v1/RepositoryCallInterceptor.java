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

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.ibm.liberty.starter.PatternValidation;
import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.PatternValidation.PatternType;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

@Path("v1/repo")
public class RepositoryCallInterceptor {

    @GET
    @Path("net/wasdev/wlp/starters/{tech}/{path: .*}")
    public Response getArtifacts(@PathParam("tech") String tech, @PathParam("path") String path, @Context UriInfo info) throws IOException {
        if (PatternValidation.checkPattern(PatternType.TECH, tech)
            && PatternValidation.checkPattern(PatternType.PATH_EXTENSION, path)) {
            String fileExtension = "net/wasdev/wlp/starters/" + tech + "/" + path;
            System.out.println("Request for artifact file " + fileExtension);
            ServiceConnector serviceConnector = new ServiceConnector(info.getBaseUri());

            //special case for microprofile-apis since it's not a tech option
            if(tech.equals("microprofile-apis")){
              try {
                  String endpoint = "/microprofile-apis";
                  InputStream is = serviceConnector.getArtifactAsInputStream(endpoint, fileExtension);
                  return Response.ok(is).build();
              } catch (Exception e) {
                  System.out.println("File " + fileExtension + " not found so returning a 404.");
                  return Response.status(Status.NOT_FOUND).entity("File not found: " + fileExtension + " not found.").build();
              }
            }

            Service service = serviceConnector.getServiceObjectFromId(tech);
            if (service == null) {
                return Response.status(Status.NOT_FOUND).entity("Tech type " + tech + " not found").build();
            }
            try {
                InputStream is = serviceConnector.getArtifactAsInputStream(service, fileExtension);
                return Response.ok(is).build();
            } catch (Exception e) {
                System.out.println("File " + fileExtension + " not found so returning a 404.");
                return Response.status(Status.NOT_FOUND).entity("File not found: " + fileExtension + " not found.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Invalid file request.").build();
        }

    }
}
