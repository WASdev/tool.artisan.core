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

public class ProjectConstructionInputData {
    public final Services services;
    public final ServiceConnector serviceConnector;
    public final String appName;
    public final ProjectConstructor.DeployType deployType;
    public final ProjectConstructor.BuildType buildType;
    public final String workspaceDirectory;
    public final String artifactId;
    public final String groupId;

    public ProjectConstructionInputData(Services services, ServiceConnector serviceConnector, String appName, ProjectConstructor.DeployType deployType, ProjectConstructor.BuildType buildType, String workspaceDirectory, String artifactId, String groupId) {
        this.services = services;
        this.serviceConnector = serviceConnector;
        this.appName = appName;
        this.deployType = deployType;
        this.buildType = buildType;
        this.workspaceDirectory = workspaceDirectory;
        this.artifactId = artifactId;
        this.groupId = groupId;
    }
}
