package com.ibm.liberty.starter.build.maven;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class AppArtifactConfigCommand implements PomModifierCommand {
    
    private static final Logger log = Logger.getLogger(AppNameCommand.class.getName());
    private String artifactId;
    private String groupId;
    private Node project;
    
    public AppArtifactConfigCommand(String artifactId, String groupId) {
        this.artifactId = artifactId;
        this.groupId = groupId;
    }

    @Override
    public void modifyPom(Document pom) throws IOException {
        log.log(Level.INFO, "Setting artifactId node to " + artifactId);
        log.log(Level.INFO, "Setting groupId node to " + groupId);
        try {
            project = pom.getElementsByTagName("project").item(0);
            setNodeTextContent("artifactId", artifactId);
            setNodeTextContent("groupId", groupId);
        } catch (UnableToFindNodeException e) {
            log.log(Level.SEVERE, "Unable to find the node for artifactId or groupId so will not be set", e);
        }
    }
    
    private void setNodeTextContent(String nodeName, String textContent) throws UnableToFindNodeException {
        Node node = DomUtil.getChildNode(project, nodeName, null);
        if (node == null) {
            throw new UnableToFindNodeException("Missing node " + nodeName);
        }
        setIfNotNull(node, textContent);
    }
    
    private void setIfNotNull(Node node, String textContent) {
        if (textContent != null) {
            node.setTextContent(textContent);
        }
    }    
    private static class UnableToFindNodeException extends Exception {

        private static final long serialVersionUID = -8095349390659588081L;

        public UnableToFindNodeException(String message) {
            super(message);
        }

    }

}
