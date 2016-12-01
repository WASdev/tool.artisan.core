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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency.Scope;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

public class MockDependencyHandler extends DependencyHandler {
    
    public MockDependencyHandler(Services services, ServiceConnector serviceConnector, String appName) {
        super(services, serviceConnector, appName);
    }

    private Map<String, Dependency> providedDependency = new HashMap<String, Dependency>();
    private Map<String, Dependency> runtimeDependency = new HashMap<String, Dependency>();
    private Map<String, Dependency> compileDependency = new HashMap<String, Dependency>();
    
    public Map<String, Dependency>  getProvidedDependency() {
        return providedDependency;
    }
    public Map<String, Dependency>  getRuntimeDependency() {
        return runtimeDependency;
    }
    public Map<String, Dependency> getCompileDependency() {
        return compileDependency;
    }
    
    private static Dependency createDependency(Scope scope) {
        Dependency dependency = new Dependency();
        dependency.setGroupId("net.wasdev.wlp.starters.wibble");
        dependency.setArtifactId(scope.name().toLowerCase() + "ArtifactId");
        dependency.setScope(scope);
        dependency.setVersion("0.0.1");
        return dependency;
    }
    
    public static DependencyHandler getDefaultInstance() throws URISyntaxException {
        URI uri = new URI("http://mock");
        Dependency[] dependencies = new Dependency[2];
        dependencies[0] = createDependency(Dependency.Scope.PROVIDED);
        dependencies[1] = createDependency(Dependency.Scope.RUNTIME);
        MockServiceConnector serviceConnector = new MockServiceConnector(uri, dependencies);
        DependencyHandler depHand = new DependencyHandler(getServices(), serviceConnector, null);
        return depHand;
    }
    
    public static DependencyHandler getProvidedInstance() throws URISyntaxException {
        URI uri = new URI("");
        Dependency[] dependencies = new Dependency[1];
        dependencies[0] = createDependency(Dependency.Scope.PROVIDED);
        MockServiceConnector serviceConnector = new MockServiceConnector(uri, dependencies);
        DependencyHandler depHand = new DependencyHandler(getServices(), serviceConnector, null);
        return depHand;
    }

    public static DependencyHandler getCompileInstance() throws URISyntaxException {
        URI uri = new URI("");
        Dependency[] dependencies = new Dependency[1];
        dependencies[0] = createDependency(Scope.COMPILE);
        MockServiceConnector serviceConnector = new MockServiceConnector(uri, dependencies);
        DependencyHandler depHand = new DependencyHandler(getServices(), serviceConnector, null);
        return depHand;
    }
    
    public static DependencyHandler getProvidedDuplicateInstance() throws URISyntaxException {
        URI uri = new URI("");
        Dependency[] dependencies = new Dependency[2];
        dependencies[0] = createDependency(Dependency.Scope.PROVIDED);
        dependencies[1] = createDependency(Dependency.Scope.PROVIDED);
        MockServiceConnector serviceConnector = new MockServiceConnector(uri, dependencies);
        DependencyHandler depHand = new DependencyHandler(getServices(), serviceConnector, null);
        return depHand;
    }
    
    public static DependencyHandler getDependencyHandlerWithName(String name) throws URISyntaxException {
        URI uri = new URI("");
        Dependency[] dependencies = new Dependency[1];
        dependencies[0] = createDependency(Dependency.Scope.PROVIDED);
        MockServiceConnector serviceConnector = new MockServiceConnector(uri, dependencies);
        DependencyHandler depHand = new DependencyHandler(getServices(), serviceConnector, name);
        return depHand;
    }

    private static Services getServices() {
        Services services = new Services();
        Service service = new Service();
        service.setId("wibble");
        List<Service> serviceList = new ArrayList<Service>();
        serviceList.add(service);
        services.setServices(serviceList);
        return services;
    }

}
