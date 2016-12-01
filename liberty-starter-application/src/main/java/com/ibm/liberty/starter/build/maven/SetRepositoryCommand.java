package com.ibm.liberty.starter.build.maven;

import com.ibm.liberty.starter.DependencyHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetRepositoryCommand implements PomModifierCommand {

    private final String repoUrl;
    private Logger log = Logger.getLogger(SetRepositoryCommand.class.getName());

    public SetRepositoryCommand(DependencyHandler dependencyHandler) {
        this.repoUrl = dependencyHandler.getRepositoryUrl();
    }

    @Override
    public void modifyPom(Document pom) throws IOException {
        log.log(Level.INFO, "Append repo url of " + repoUrl);
        NodeList repoNodeList = pom.getElementsByTagName("repository");
        boolean foundRepoNode = false;
        for (int i = 0; i < repoNodeList.getLength(); i++) {
            Element repoNode = (Element) repoNodeList.item(i);
            if (DomUtil.nodeHasId(repoNode, "liberty-starter-maven-repo")) {
                foundRepoNode = true;
                Node urlNode = pom.createElement("url");
                urlNode.setTextContent(repoUrl);
                repoNode.appendChild(urlNode);
                break;
            }
        }
        if (!foundRepoNode) {
            throw new IOException("Repository url node not found in pom input");
        }
    }

}
