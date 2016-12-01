package com.ibm.liberty.starter.build.maven;

import com.ibm.liberty.starter.build.FeaturesToInstallProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class AddFeaturesCommand implements PomModifierCommand {

    private final List<String> listOfFeatures;
    
    public AddFeaturesCommand(FeaturesToInstallProvider featureProvider) {
        listOfFeatures = featureProvider.getFeatures();
    }

    @Override
    public void modifyPom(Document doc) throws IOException {
        if (!listOfFeatures.isEmpty()) {
            Node features;
            Node configuration = getLibertyMavenPluginConfiguration(doc);
            features = DomUtil.findOrAddChildNode(doc, configuration, "features", null);

            addInstallFeatureExecution(doc, configuration.getParentNode());

            if (!DomUtil.hasChildNode(features, "acceptLicense")) {
                DomUtil.addChildNode(doc, features, "acceptLicense", "${accept.features.license}");
                enforceAcceptLicenseProperty(doc, configuration.getParentNode().getParentNode());
            }

            //Add the features
            for (String feature : listOfFeatures) {
                DomUtil.findOrAddChildNode(doc, features, "feature", feature);
            }
        }
    }

    private void addInstallFeatureExecution(Document doc, Node libertyPluginNode) {
        Node executions = DomUtil.findOrAddChildNode(doc, libertyPluginNode, "executions", null);
        Node execution = DomUtil.addChildNode(doc, executions, "execution", null);
        DomUtil.addChildNode(doc, execution, "id", "install-feature");
        DomUtil.addChildNode(doc, execution, "phase", "prepare-package");
        Node goals = DomUtil.addChildNode(doc, execution, "goals", null);
        DomUtil.addChildNode(doc, goals, "goal", "install-feature");
    }

    private Node getLibertyMavenPluginConfiguration(Document doc) {
        Node assemblyInstallDirectory = doc.getElementsByTagName("assemblyInstallDirectory").item(0);
        return assemblyInstallDirectory.getParentNode();
    }

    private void enforceAcceptLicenseProperty(Document doc, Node pluginsNode) {
        //Enforce 'accept.features.license' property using maven-enforcer-plugin
        Node enforcerPlugin;
        Node artifactIdNode = DomUtil.getGrandchildNode(pluginsNode, "plugin", "artifactId", "maven-enforcer-plugin");
        if (artifactIdNode == null) {
            enforcerPlugin = DomUtil.addChildNode(doc, pluginsNode, "plugin", null);
            DomUtil.addChildNode(doc, enforcerPlugin, "groupId", "org.apache.maven.plugins");
            DomUtil.addChildNode(doc, enforcerPlugin, "artifactId", "maven-enforcer-plugin");
            DomUtil.addChildNode(doc, enforcerPlugin, "version", "1.4.1");
        } else {
            enforcerPlugin = artifactIdNode.getParentNode();
        }

        Node executions = DomUtil.findOrAddChildNode(doc, enforcerPlugin, "executions", null);

        Node execution;
        Node enforceProperty = DomUtil.getGrandchildNode(executions, "execution", "id", "enforce-property");
        if (enforceProperty == null) {
            execution = DomUtil.addChildNode(doc, executions, "execution", null);
            DomUtil.addChildNode(doc, execution, "id", "enforce-property");
        } else {
            execution = enforceProperty.getParentNode();
        }

        DomUtil.findOrAddChildNode(doc, execution, "phase", "validate");

        Node goals = DomUtil.findOrAddChildNode(doc, execution, "goals", null);
        DomUtil.findOrAddChildNode(doc, goals, "goal", "enforce");

        Node configurationNode = DomUtil.findOrAddChildNode(doc, execution, "configuration", null);
        Node rules = DomUtil.findOrAddChildNode(doc, configurationNode, "rules", null);
        Node propertyNode = DomUtil.getGrandchildNode(rules, "requireProperty", "property", "accept.features.license");
        if (propertyNode == null) {
            Node requireProperty = DomUtil.addChildNode(doc, rules, "requireProperty", null);
            DomUtil.addChildNode(doc, requireProperty, "property", "accept.features.license");
            DomUtil.addChildNode(doc, requireProperty, "message",
                                 "You must set a value for the 'accept.features.license' property defined in pom.xml. Please review the license terms and conditions for additional features to be installed and if you accept the license terms and conditions then run the Maven command with '-Daccept.features.license=true'.");
            DomUtil.addChildNode(doc, requireProperty, "regex", "true");
            DomUtil.addChildNode(doc, requireProperty, "regexMessage",
                                 "Additional features could not be installed as the license terms and conditions were not accepted. If you accept the license terms and conditions then run the Maven command with '-Daccept.features.license=true'.");
        }

        DomUtil.findOrAddChildNode(doc, configurationNode, "fail", "true");
    }

}
