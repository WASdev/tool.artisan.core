package com.ibm.liberty.starter.build.maven;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AppArtifactConfigCommand implements PomModifierCommand {
    
    private static final Logger log = Logger.getLogger(AppNameCommand.class.getName());
    private String artifactId;
    private String groupId;
    private final String defaultGroupId = "liberty.maven";
    private final String defaultArtifactId = "test";
    
    public AppArtifactConfigCommand(String artifactId, String groupId) {
        this.artifactId = artifactId;
        this.groupId = groupId;
    }

    @Override
    public void modifyPom(Document pom) throws IOException {
        log.log(Level.INFO, "Setting artifactId node to " + artifactId);
        log.log(Level.INFO, "Setting groupId node to " + groupId);
        try {
            Node project = pom.getElementsByTagName("project").item(0);
            Node artifactIdNode = DomUtil.getChildNode(project, "artifactId", defaultArtifactId);
            Node groupIdNode = DomUtil.getChildNode(project, "groupId", defaultGroupId);
            if (artifactIdNode == null || groupIdNode == null) { 
                throw new UnableToFindNodeException("GroupId or artifactId not set");
            }
            artifactIdNode.setNodeValue(artifactId);
            groupIdNode.setNodeValue(groupId);
        } catch (UnableToFindNodeException e) {
            log.log(Level.SEVERE, "Unable to find the node for artifactId or groupId so one or both will not be set", e);
        }
    }
    
    private static class UnableToFindNodeException extends Exception {

        private static final long serialVersionUID = -8095349390659588081L;

        public UnableToFindNodeException(String message) {
            super(message);
        }

    }

}
