package com.ibm.liberty.starter.build.maven;

import org.w3c.dom.Document;

import java.io.IOException;

public interface PomModifierCommand {

    public void modifyPom(Document pom) throws IOException;

}
