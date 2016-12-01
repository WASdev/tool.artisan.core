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

import java.util.HashMap;
import java.util.Map;

import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency.Scope;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class DependencyHandler {

    private final ServiceConnector serviceConnector;
    private final String appName;
    private final String serverHostPort;

    private Map<String, Dependency> providedDependency = new HashMap<String, Dependency>();
    private Map<String, Dependency> runtimeDependency = new HashMap<String, Dependency>();
    private Map<String, Dependency> compileDependency = new HashMap<String, Dependency>();

    public DependencyHandler(Services services, ServiceConnector serviceConnector, String appName) {
        this.serviceConnector = serviceConnector;
        this.appName = appName;
        this.serverHostPort = serviceConnector.getServerHostPort();
        setServices(services);
    }
    
    private void setServices(Services services) {
        for (Service service : services.getServices()) {
            System.out.println("Creating DependencyHandler for service " + service.getId());
            Provider provider = serviceConnector.getProvider(service);
            Dependency[] dependencies = provider.getDependencies();
            for (Dependency dependency : dependencies) {
                Scope scope = dependency.getScope();
                System.out.println("Setting scope " + scope + " to " + dependency);
                switch (scope) {
                    case PROVIDED:
                        providedDependency.put(service.getId(), dependency);
                        break;
                    case COMPILE:
                        compileDependency.put(service.getId(), dependency);
                        break;
                    case RUNTIME:
                        runtimeDependency.put(service.getId(), dependency);
                        break;
                }
            }
        }
    }

    public Map<String, Dependency> getProvidedDependency() {
        return providedDependency;
    }

    public Map<String, Dependency> getRuntimeDependency() {
        return runtimeDependency;
    }

    public Map<String, Dependency> getCompileDependency() {
        return compileDependency;
    }
    
    public String getAppName() {
        return appName != null ? appName : "LibertyProject";
    }
    
    public String getServerHostPort() {
        return serverHostPort;
    }

    public String getRepositoryUrl() {
        return getServerHostPort() + "/start/api/v1/repo";
    }

}
