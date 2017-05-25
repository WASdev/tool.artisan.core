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
package com.ibm.liberty.starter.build.maven.unit;

import com.ibm.liberty.starter.build.maven.DomUtil;
import com.ibm.liberty.starter.build.maven.SetRepositoryCommand;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SetRepositoryCommandTest {

    @Test
    public void setsLibertyRepositoryUrl() throws Exception {
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node repositories = DomUtil.addChildNode(pom, project, "repositories", null);
        Node repository = DomUtil.addChildNode(pom, repositories, "repository", null);
        DomUtil.addChildNode(pom, repository, "id", "liberty-starter-maven-repo");
        SetRepositoryCommand testObject = new SetRepositoryCommand(MockDependencyHandler.getDefaultInstance());

        testObject.modifyPom(pom);

        Node urlNode = DomUtil.getChildNode(repository, "url", null);
        assertThat(urlNode, notNullValue());
        assertThat(urlNode.getTextContent(), is("http://mock/start/api/v1/repo"));
    }

}
