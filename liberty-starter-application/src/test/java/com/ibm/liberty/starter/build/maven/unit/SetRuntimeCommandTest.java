/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
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
package com.ibm.liberty.starter.build.maven.unit;

import com.ibm.liberty.starter.build.maven.DomUtil;
import com.ibm.liberty.starter.build.maven.SetRepositoryCommand;
import com.ibm.liberty.starter.build.maven.SetRuntimeCommand;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SetRuntimeCommandTest {

    @Test
    public void setsLibertyBetaRuntimeUrl() throws Exception {
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node plugins = DomUtil.addChildNode(pom, project, "plugins", null);
        Node plugin = DomUtil.addChildNode(pom, plugins, "plugin", null);
        Node artifactId = DomUtil.addChildNode(pom, plugin, "artifactId", "liberty-maven-plugin");
        Node configuration = DomUtil.addChildNode(pom, plugin, "configuration", null);    
        SetRuntimeCommand testObject = new SetRuntimeCommand(true);    
        testObject.modifyPom(pom);

        Node configNode = DomUtil.getChildNode(plugin, "configuration", null);
        assertThat(configNode, notNullValue());
        System.out.println("configuration is " + configNode.getTextContent());
        assertThat(configNode.getTextContent(), is("webProfile72017.+"));
    }

    @Test
    public void notSetLibertyBetaRuntimeUrl() throws Exception {
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node plugins = DomUtil.addChildNode(pom, project, "plugins", null);
        Node plugin = DomUtil.addChildNode(pom, plugins, "plugin", null);
        Node artifactId = DomUtil.addChildNode(pom, plugin, "artifactId", "liberty-maven-plugin");
        Node configuration = DomUtil.addChildNode(pom, plugin, "configuration", null);    
        SetRuntimeCommand testObject = new SetRuntimeCommand(false);
      
        testObject.modifyPom(pom);

        Node configNode = DomUtil.getChildNode(plugin, "configuration", null);
        assertThat(configNode, notNullValue());
        assertThat(configNode.getTextContent(), is("com.ibm.websphere.appserver.runtimewlp-webProfile717.0.0.1zip"));
    }
}
