package com.ibm.liberty.starter.build.maven.unit;

import com.ibm.liberty.starter.build.maven.PomModifier;
import com.ibm.liberty.starter.build.maven.PomModifierCommand;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static com.ibm.liberty.starter.ByteMatcher.isByteArrayFor;
import static org.junit.Assert.assertThat;

public class PomModifierTest {

    private InputStream simplePomInputStream;

    @Before
    public void createSimpleInputStream() {
        byte[] pomBytes = "<project></project>".getBytes();
        simplePomInputStream = new ByteArrayInputStream(pomBytes);
    }

    @Test
    public void canReadAndWritePoms() throws TransformerException, IOException, ParserConfigurationException, SAXException {
        PomModifier testObject = new PomModifier(simplePomInputStream, Collections.emptySet());

        byte[] outputBytes = testObject.getPomBytes();

        assertThat(outputBytes, isByteArrayFor("<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><project/>"));
    }

    @Test
    public void canAlterPom() throws TransformerException, IOException, ParserConfigurationException, SAXException {
        PomModifierCommand fakeCommand = pom -> {
            Node wibbleNode = pom.createElement("wibble");
            pom.getDocumentElement().appendChild(wibbleNode);
        };
        PomModifier testObject = new PomModifier(simplePomInputStream, Collections.singleton(fakeCommand));

        byte[] outputBytes = testObject.getPomBytes();

        assertThat(outputBytes, isByteArrayFor("<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><project><wibble/></project>"));
    }

}
