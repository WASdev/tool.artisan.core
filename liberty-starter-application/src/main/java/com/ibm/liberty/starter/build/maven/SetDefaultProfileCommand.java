package com.ibm.liberty.starter.build.maven;

import com.ibm.liberty.starter.ProjectZipConstructor.DeployType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SetDefaultProfileCommand implements PomModifierCommand {

    private final DeployType deployType;
    private static final Logger log = Logger.getLogger(SetDefaultProfileCommand.class.getName());

    public SetDefaultProfileCommand(DeployType deployType) {
        this.deployType = deployType;
    }

    @Override
    public void modifyPom(Document pom) {
        if (DeployType.BLUEMIX.equals(deployType)) {
            log.log(Level.INFO, "PomModifier adding profile activation for bluemix");
            try {
                Node profileNode = getProfileNodeById("bluemix", pom);
                Node activationNode = pom.createElement("activation");
                Node activeByDefault = pom.createElement("activeByDefault");
                activeByDefault.setTextContent("true");
                activationNode.appendChild(activeByDefault);
                profileNode.appendChild(activationNode);
            } catch (UnableToFindNodeException e) {
                log.log(Level.SEVERE, "Unable to find the profile for " + deployType + " so not default activation will be set", e);
            }
        }
    }

    private Node getProfileNodeById(String nodeId, Document doc) throws UnableToFindNodeException {
        NodeList profileNodeList = doc.getElementsByTagName("profile");
        int length = profileNodeList.getLength();
        for (int i = 0; i < length; i++) {
            Node profileNode = profileNodeList.item(i);
            if (DomUtil.nodeHasId(profileNode, nodeId)) {
                return profileNode;
            }
        }
        throw new UnableToFindNodeException(nodeId);
    }

    private static class UnableToFindNodeException extends Exception {

        private static final long serialVersionUID = -8095349390659588081L;

        public UnableToFindNodeException(String message) {
            super(message);
        }

    }

}
