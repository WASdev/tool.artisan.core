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
