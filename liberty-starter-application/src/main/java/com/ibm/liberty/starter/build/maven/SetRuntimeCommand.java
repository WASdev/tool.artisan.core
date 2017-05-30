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
package com.ibm.liberty.starter.build.maven;

import com.ibm.liberty.starter.DependencyHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetRuntimeCommand implements PomModifierCommand {

    private boolean isBeta = false;
    private Logger log = Logger.getLogger(SetRuntimeCommand.class.getName());

    public SetRuntimeCommand(boolean beta) {
        isBeta = beta;
        log.log(Level.INFO, "SetRuntimeCommand isBeta " + beta);
    }

    @Override
    public void modifyPom(Document pom) throws IOException {
        log.log(Level.INFO, "Append liberty image for isBeta " + isBeta);
        NodeList pluginNodeList = pom.getElementsByTagName("plugin");
        boolean foundPluginNode = false;
        for (int i = 0; i < pluginNodeList.getLength(); i++) {
            Element pluginNode = (Element) pluginNodeList.item(i);
            if (DomUtil.nodeHasId(pluginNode, "liberty-maven-plugin")) {
            	NodeList configNodeList = pluginNode.getElementsByTagName("configuration");
            	for (int j = 0; j < configNodeList.getLength(); j++) {
            		Node configNode = (Node) configNodeList.item(j);
            		if (isBeta) {
            			Node installNode = pom.createElement("install");
            			configNode.appendChild(installNode);
            			Node typeNode = pom.createElement("type");           			
            			typeNode.setTextContent("webProfile7");
            			installNode.appendChild(typeNode);
            			Node versionNode = pom.createElement("version");
            			versionNode.setTextContent("2017.+");
            			installNode.appendChild(versionNode);
            			foundPluginNode = true;
            		} else {
            			Node assemblyNode = pom.createElement("assemblyArtifact");
            			configNode.appendChild(assemblyNode);
            			Node groupIdNode = pom.createElement("groupId");
            			groupIdNode.setTextContent("com.ibm.websphere.appserver.runtime");
            			assemblyNode.appendChild(groupIdNode);
            			Node artifactIdNode = pom.createElement("artifactId");
            			artifactIdNode.setTextContent("wlp-webProfile7");
            			assemblyNode.appendChild(artifactIdNode);
            			Node versionNode = pom.createElement("version");
            			versionNode.setTextContent("17.0.0.1");
            			assemblyNode.appendChild(versionNode);
            			Node typeNode = pom.createElement("type");
             			typeNode.setTextContent("zip");    
             			assemblyNode.appendChild(typeNode);
             			foundPluginNode = true;
            		}
            		if (foundPluginNode) 
            			break;
            	}
            }
        }
        if (!foundPluginNode) {
            throw new IOException("Plugin url node not found in pom input");
        }
    }

}
