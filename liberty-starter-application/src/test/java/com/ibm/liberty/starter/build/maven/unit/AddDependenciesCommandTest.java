package com.ibm.liberty.starter.build.maven.unit;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.build.maven.AddDependenciesCommand;
import com.ibm.liberty.starter.build.maven.DomUtil;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AddDependenciesCommandTest {

    private Document pom;

    @Before
    public void createPom() throws ParserConfigurationException {
        pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node project = DomUtil.addChildNode(pom, pom, "project", null);
        DomUtil.addChildNode(pom, project, "dependencies", null);
    }

    @Test
    public void addsDependenciesToPom() throws URISyntaxException {
        DependencyHandler depHand = MockDependencyHandler.getDefaultInstance();
        AddDependenciesCommand testObject = new AddDependenciesCommand(depHand);

        testObject.modifyPom(pom);

        NodeList dependencyNodes = pom.getElementsByTagName("dependency");
        assertThat(dependencyNodes.getLength(), is(2));
        boolean hasRuntimeDependency = false;
        boolean hasProvidedDependency = false;
        for (int i = 0; i < 2; i++) {
            Node dependencyNode = dependencyNodes.item(i);
            DependencyInformation dependencyInformation = new DependencyInformation(dependencyNode);
            assertCoreFieldsAreSetOnDependency(dependencyInformation);
            switch (dependencyInformation.scope) {
                case "runtime":
                    assertThat(dependencyInformation.artifactId, is("runtimeArtifactId"));
                    hasRuntimeDependency = true;
                    break;
                case "provided":
                    assertThat(dependencyInformation.artifactId, is("providedArtifactId"));
                    hasProvidedDependency = true;
                    break;
                default:
                    fail("Unexpected dependency scope: " + dependencyInformation.scope);
            }
        }
        assertThat(hasRuntimeDependency, is(true));
        assertThat(hasProvidedDependency, is(true));
    }

    @Test
    public void canAddDependencyWithoutConfigPom() throws Exception {
        DependencyHandler depHand = MockDependencyHandler.getProvidedInstance();
        AddDependenciesCommand testObject = new AddDependenciesCommand(depHand);

        testObject.modifyPom(pom);

        assertThereIsOnlyASingleProvidedDependency();
    }

    @Test
    public void duplicatesAreAddedAsSingleDependency() throws Exception {
        DependencyHandler depHand = MockDependencyHandler.getProvidedDuplicateInstance();
        AddDependenciesCommand testObject = new AddDependenciesCommand(depHand);

        testObject.modifyPom(pom);

        assertThereIsOnlyASingleProvidedDependency();
    }

    private void assertThereIsOnlyASingleProvidedDependency() {
        NodeList dependencyNodes = pom.getElementsByTagName("dependency");
        assertThat(dependencyNodes.getLength(), is(1));
        DependencyInformation dependencyInformation = new DependencyInformation(dependencyNodes.item(0));
        assertCoreFieldsAreSetOnDependency(dependencyInformation);
        assertThat(dependencyInformation.scope, is("provided"));
        assertThat(dependencyInformation.artifactId, is("providedArtifactId"));
    }

    private void assertCoreFieldsAreSetOnDependency(DependencyInformation dependencyInformation) {
        assertThat(dependencyInformation.groupId, is("net.wasdev.wlp.starters.wibble"));
        assertThat(dependencyInformation.version, is("0.0.1"));
        assertThat(dependencyInformation.type, is("pom"));
    }

    private static class DependencyInformation {
        private String groupId;
        private String artifactId;
        private String version;
        private String type;
        private String scope;

        public DependencyInformation(Node dependencyNode) {
            NodeList children = dependencyNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                switch (child.getNodeName()) {
                    case "groupId":
                        groupId = child.getTextContent();
                        break;
                    case "artifactId":
                        artifactId = child.getTextContent();
                        break;
                    case "version":
                        version = child.getTextContent();
                        break;
                    case "type":
                        type = child.getTextContent();
                        break;
                    case "scope":
                        scope = child.getTextContent();
                        break;
                    default:
                        fail("Unexpected child node: " + child.getNodeName());
                }
            }
        }
    }
}
