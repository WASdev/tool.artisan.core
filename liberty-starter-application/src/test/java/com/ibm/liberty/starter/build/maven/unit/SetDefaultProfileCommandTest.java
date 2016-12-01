package com.ibm.liberty.starter.build.maven.unit;

import com.ibm.liberty.starter.ProjectZipConstructor.DeployType;
import com.ibm.liberty.starter.build.maven.DomUtil;
import com.ibm.liberty.starter.build.maven.SetDefaultProfileCommand;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SetDefaultProfileCommandTest {

    private Document pom;
    private Node bluemixProfile;

    @Before
    public void setupTemplatePom() throws ParserConfigurationException {
        pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node profiles = DomUtil.addChildNode(pom, project, "profiles", null);
        bluemixProfile = addProfile(pom, profiles, "bluemix");
    }

    @Test
    public void settingDeployTypeToLocalDoesntBreakAnything() throws Exception {
        SetDefaultProfileCommand testObject = new SetDefaultProfileCommand(DeployType.LOCAL);

        testObject.modifyPom(pom);

        assertProfileIsNotActiveByDefault(bluemixProfile);
    }

    @Test
    public void canSetBluemixAsDefaultProfile() throws Exception {
        SetDefaultProfileCommand testObject = new SetDefaultProfileCommand(DeployType.BLUEMIX);

        testObject.modifyPom(pom);
        
        assertProfileIsActiveByDefault(bluemixProfile);
    }

    private void assertProfileIsNotActiveByDefault(Node profile) {
        assertThat(profile.getChildNodes().getLength(), is(1));
    }

    private void assertProfileIsActiveByDefault(Node profile) {
        assertThat(profile.getChildNodes().getLength(), is(2));
        Node activationNode = DomUtil.getChildNode(profile, "activation", null);
        assertThat(activationNode, notNullValue());
        Node activeByDefaultNode = DomUtil.getChildNode(activationNode, "activeByDefault", "true");
        assertThat(activeByDefaultNode, notNullValue());
    }

    private Node addProfile(Document pom, Node profiles, String name) {
        Node profile = DomUtil.addChildNode(pom, profiles, "profile", null);
        Node id = DomUtil.addChildNode(pom, profile, "id", null);
        id.setTextContent(name);
        return profile;
    }
}
