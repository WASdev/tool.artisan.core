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
package com.ibm.liberty.starter.service.springboot.api.v1.it;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.Sample;
import com.ibm.liberty.starter.api.v1.model.provider.ServerConfig;

/**
 * Test the deployed service responds as expected
 *
 */
public class TestApplication extends EndpointTest {
	
	@Before
	public void checkSetup() {
		checkAvailability("/api/v1/provider/");
	}

    @Test
    public void testProvider() throws Exception {
        Provider provider =  testEndpoint("/api/v1/provider/", Provider.class);
        assertNotNull("No response from API for provider", provider);
        assertTrue("Description was not found.", provider.getDescription().contains("<h2>Spring Boot with Spring MVC</h2>"));
        Dependency[] dependencies = provider.getDependencies();
        boolean providedDependency = false;
        boolean runtimeDependency = false;
        boolean compileDependency = false;
        for (Dependency dependency : dependencies) {
            if (Dependency.Scope.PROVIDED.equals(dependency.getScope())) {
                assertTrue("groupId incorrect.", "net.wasdev.wlp.starters.springbootweb".equals(dependency.getGroupId()));
                assertTrue("artifactId incorrect.", "provided-pom".equals(dependency.getArtifactId()));
                assertTrue("version incorrect.", "0.0.2".equals(dependency.getVersion()));
                providedDependency = true;
            }
            if (Dependency.Scope.RUNTIME.equals(dependency.getScope())) {
                assertTrue("groupId incorrect.", "net.wasdev.wlp.starters.springbootweb".equals(dependency.getGroupId()));
                assertTrue("artifactId incorrect.", "runtime-pom".equals(dependency.getArtifactId()));
                assertTrue("version incorrect.", "0.0.2".equals(dependency.getVersion()));
                runtimeDependency = true;
            }
            if (Dependency.Scope.COMPILE.equals(dependency.getScope())) {
                assertTrue("groupId incorrect.", "net.wasdev.wlp.starters.springbootweb".equals(dependency.getGroupId()));
                assertTrue("artifactId incorrect.", "compile-pom".equals(dependency.getArtifactId()));
                assertTrue("version incorrect.", "0.0.2".equals(dependency.getVersion()));
                compileDependency = true;
            }
        }
        assertTrue("Provided dependencies weren't specified.", providedDependency);
        assertTrue("Runtime dependencies weren't specified.", runtimeDependency);
        assertTrue("Compile dependencies weren't specified.", compileDependency);
    }
    
    @Test
    public void testConfig() throws Exception {
    	ServerConfig config = testEndpoint("/api/v1/provider/config", ServerConfig.class);
    	assertNotNull("No response from API for configuration", config);
    	String actual = config.getTags()[0].getTags()[0].getValue();
    	String expected = "websocket-1.1";
    	assertEquals("Incorrect feature specified", expected , actual);
    }
    
    @Test
    public void testSamples() throws Exception {
    	Sample sample = testEndpoint("/api/v1/provider/samples", Sample.class);
    	assertNotNull("No response from API for sample", sample);
    	assertNotNull("Expected locations", sample.getLocations());
    	assertEquals("No files were expected for sample", 3, sample.getLocations().length);
    }

}
