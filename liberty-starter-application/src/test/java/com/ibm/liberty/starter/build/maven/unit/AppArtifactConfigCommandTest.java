package com.ibm.liberty.starter.build.maven.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ibm.liberty.starter.build.maven.AppArtifactConfigCommand;
import com.ibm.liberty.starter.build.maven.DomUtil;

public class AppArtifactConfigCommandTest {

    private Document pom;
    private Node project;

    @Before
    public void setupTemplatePom() throws ParserConfigurationException {
        pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        project = DomUtil.addChildNode(pom, pom, "project", null);
        DomUtil.addChildNode(pom, project, "artifactId", "liberty.maven");
        DomUtil.addChildNode(pom, project, "groupId", "test");
    }
    
    @Test
    public void setArtifactId() throws IOException {
        String testArtifactId = "testId";
        AppArtifactConfigCommand configCommand = new AppArtifactConfigCommand(testArtifactId, "test.group");
        configCommand.modifyPom(pom);
        String modifiedArtifactId = getPomElementById("artifactId");
        assertThat(modifiedArtifactId, is(testArtifactId));
    }
    
    @Test
    public void setGroupId() throws IOException {
        String testGroupId = "test.group.id";
        AppArtifactConfigCommand configCommand = new AppArtifactConfigCommand("test", testGroupId);
        configCommand.modifyPom(pom);
        String modifiedArtifactId = getPomElementById("groupId");
        assertThat(modifiedArtifactId, is(testGroupId));
    }
    
    private String getPomElementById(String id) {
    	Node node = DomUtil.getChildNode(project, id, null);
    	return node.getTextContent();
    }
}
