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
package com.ibm.liberty.starter;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;
import io.jsonwebtoken.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProjectConstructionInput {

    private static final Logger log = Logger.getLogger(ProjectConstructionInput.class.getName());
    private static final String SERVICE_IDS_KEY = "serviceIds";
    private static final String BUILD_KEY = "build";
    private static final String WORKSPACE_DIR_KEY = "workspaceDir";
    private static final String DEPLOY_KEY = "deploy";
    private static final String NAME_KEY = "name";
    private static final String GROUP_ID_KEY = "groupId";
    private static final String ARTIFACT_ID_KEY = "artifactId";
    private static final String BETA_KEY = "beta";
    private final ServiceConnector serviceConnector;

    public ProjectConstructionInput(ServiceConnector serviceConnector) {
        this.serviceConnector = serviceConnector;
    }

    public ProjectConstructionInputData processInput(String[] techs, String[] techOptions, String name, String deploy, String workspaceId, String build, String artifactId, String groupId, boolean beta) {
        List<Service> serviceList = new ArrayList<Service>();
        for (String tech : techs) {
            if (PatternValidation.checkPattern(PatternValidation.PatternType.TECH, tech)) {
                Service service = serviceConnector.getServiceObjectFromId(tech);
                if (service != null) {
                    serviceList.add(service);
                    if (workspaceId != null && !workspaceId.trim().isEmpty()) {
                        serviceConnector.prepareDynamicPackages(service, StarterUtil.getWorkspaceDir(workspaceId) + "/" + service.getId(), getTechOptions(techOptions, tech), techs);
                    }
                }
            } else {
                log.info("Invalid tech type: " + tech);
                throw new ValidationException("Invalid technology type.");
            }
        }
        Services services = new Services();
        services.setServices(serviceList);
        if (name == null || name.length() == 0) {
            log.severe("No name passed in.");
            throw new ValidationException();
        }
        if (!PatternValidation.checkPattern(PatternValidation.PatternType.NAME, name)) {
            log.severe("Invalid file name.");
            throw new ValidationException();
        }

        if (name.length() > 50) {
            log.severe("Invalid file name length.");
            throw new ValidationException();
        }

        if (deploy == null) {
            log.severe("No deploy type specified");
            throw new ValidationException();
        }
        ProjectConstructor.DeployType deployType = ProjectConstructor.DeployType.valueOf(deploy.toUpperCase());
        ProjectConstructor.BuildType buildType;
        try {
            buildType = ProjectConstructor.BuildType.valueOf(build.toUpperCase());
        } catch (Exception e) {
            buildType = ProjectConstructor.BuildType.MAVEN;
        }
        if (artifactId != null && !PatternValidation.checkPattern(PatternValidation.PatternType.ARTIFACT_ID, artifactId)) {
            log.severe("Invalid artifactId.");
            throw new ValidationException();
        }
        
        if (groupId != null && !PatternValidation.checkPattern(PatternValidation.PatternType.ARTIFACT_ID, groupId)) {
            log.severe("Invalid groupId.");
            throw new ValidationException();
        }
        return new ProjectConstructionInputData(services, serviceConnector, name, deployType, buildType, StarterUtil.getWorkspaceDir(workspaceId), artifactId, groupId, beta);
    }

    public String processInputAsJwt(String[] techs, String[] techOptions, String name, String deploy, String workspaceId, String build, String artifactId, String groupId, boolean beta) throws NamingException {
        ProjectConstructionInputData inputData = processInput(techs, techOptions, name, deploy, workspaceId, build, artifactId, groupId, beta);

        Claims claims = Jwts.claims();
        claims.put(NAME_KEY, inputData.appName);
        claims.put(DEPLOY_KEY, inputData.deployType);
        claims.put(WORKSPACE_DIR_KEY, inputData.workspaceDirectory);
        claims.put(BUILD_KEY, inputData.buildType);
        claims.put(ARTIFACT_ID_KEY, inputData.artifactId);
        claims.put(GROUP_ID_KEY, inputData.groupId);
        claims.put(BETA_KEY, inputData.beta);
        claims.put(SERVICE_IDS_KEY, inputData.services.getServices().stream().map(service -> service.getId()).collect(Collectors.toList()));
        
        Calendar issuedAt = Calendar.getInstance();
        claims.setIssuedAt(issuedAt.getTime());
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.HOUR, 1);
        claims.setExpiration(expires.getTime());

        String signingKey = getAppAcceleratorSecret();
        String jwt = Jwts.builder().setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, signingKey).compact();

        return jwt;
    }

    public ProjectConstructionInputData processJwt(String jwt) throws NamingException {
        JwtParser parser = Jwts.parser().setSigningKey(getAppAcceleratorSecret());

        Jws<Claims> parsed = parser.parseClaimsJws(jwt);
        Claims claims = parsed.getBody();
        List<String> serviceIds = (List<String>) claims.get(SERVICE_IDS_KEY);
        List<Service> serviceList = serviceIds.stream().map(id -> serviceConnector.getServiceObjectFromId(id)).collect(Collectors.toList());
        Services services = new Services();
        services.setServices(serviceList);

        return new ProjectConstructionInputData(services,
                serviceConnector,
                (String) claims.get(NAME_KEY),
                ProjectConstructor.DeployType.valueOf((String) claims.get(DEPLOY_KEY)),
                ProjectConstructor.BuildType.valueOf((String) claims.get(BUILD_KEY)),
                (String) claims.get(WORKSPACE_DIR_KEY),
                (String) claims.get(ARTIFACT_ID_KEY),
                (String) claims.get(GROUP_ID_KEY),
                (boolean) claims.get(BETA_KEY));
    }

    private String getAppAcceleratorSecret() throws NamingException {
        return (String) new InitialContext().lookup("appAcceleratorSecret");
    }

    private String getTechOptions(String[] techOptions, String tech) {
        if(techOptions != null && tech != null && !tech.trim().isEmpty()){
            for(String option : techOptions){
                String[] s = option.split(":");
                if(s != null && s[0] != null && s[0].equals(tech)){
                    return option.substring(option.indexOf(":") + 1);
                }
            }
        }

        return "";
    }

}
