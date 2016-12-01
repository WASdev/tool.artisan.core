package com.ibm.liberty.starter.build.maven.unit;

import com.ibm.liberty.starter.build.maven.AddFeaturesCommand;
import com.ibm.liberty.starter.build.maven.DomUtil;
import com.ibm.liberty.starter.build.unit.FeaturesToInstallProviderTest;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AddFeaturesCommandTest {

    @Test
    public void featuresAreAddedToPom() throws Exception {
        Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        Node build = DomUtil.addChildNode(pom, project, "build", null);
        Node plugins = DomUtil.addChildNode(pom, build, "plugins", null);
        Node plugin = DomUtil.addChildNode(pom, plugins, "plugin", null);
        Node configuration = DomUtil.addChildNode(pom, plugin, "configuration", null);
        DomUtil.addChildNode(pom, configuration, "assemblyInstallDirectory", "${wibble}");
        final String fakeFeatureName = "Wibble";
        AddFeaturesCommand testObject = new AddFeaturesCommand(FeaturesToInstallProviderTest.createFeaturesToInstallProviderTestObject());

        testObject.modifyPom(pom);

        NodeList acceptLicenseNode = pom.getElementsByTagName("acceptLicense");
        assertThat(acceptLicenseNode.getLength(), is(1));
        assertThat(acceptLicenseNode.item(0).getTextContent(), is("${accept.features.license}"));
        assertThat(DomUtil.getGrandchildNode(plugins, "plugin", "artifactId", "maven-enforcer-plugin"), notNullValue());
        assertThat(DomUtil.getGrandchildNode(configuration, "features", "feature", fakeFeatureName), notNullValue());
        Node executions = DomUtil.getChildNode(plugin, "executions", null);
        assertThat(executions, notNullValue());
        Node installFeaturesIdNode = DomUtil.getGrandchildNode(executions, "execution", "id", "install-feature");
        assertThat(installFeaturesIdNode, notNullValue());
        Node goal = DomUtil.getGrandchildNode(installFeaturesIdNode.getParentNode(), "goals", "goal", "install-feature");
        assertThat(goal, notNullValue());
    }
}
