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
package com.ibm.liberty.starter;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProjectConstructionInput {

    private static final Logger log = Logger.getLogger(ProjectConstructionInput.class.getName());
    private final ServiceConnector serviceConnector;

    public ProjectConstructionInput(ServiceConnector serviceConnector) {
        this.serviceConnector = serviceConnector;
    }

    public ProjectConstructionInputData processInput(String[] techs, String[] techOptions, String name, String deploy, String workspaceId, String build, String artifactId, String groupId) {
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
        return new ProjectConstructionInputData(services, serviceConnector, name, deployType, buildType, StarterUtil.getWorkspaceDir(workspaceId), artifactId, groupId);
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
