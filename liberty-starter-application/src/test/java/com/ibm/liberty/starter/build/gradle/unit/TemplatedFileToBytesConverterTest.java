package com.ibm.liberty.starter.build.gradle.unit;

import com.ibm.liberty.starter.build.gradle.TemplatedFileToBytesConverter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.ibm.liberty.starter.ByteMatcher.isByteArrayIncludingSpacesFor;
import static org.junit.Assert.assertThat;

public class TemplatedFileToBytesConverterTest {

    @Test
    public void templatedFileWithNoTagsUnchanged() throws IOException {
        String fileContents = "line1\nline2";
        TemplatedFileToBytesConverter testObject = createTestObject(fileContents, Collections.emptyMap());

        byte[] output = testObject.getBytes();

        assertThat(output, isByteArrayIncludingSpacesFor(fileContents));
    }

    @Test
    public void lineRemovedWhenOnlyContainsMissingTag() throws IOException {
        String fileContents = "line1\n %WIBBLE% ";
        TemplatedFileToBytesConverter testObject = createTestObject(fileContents, Collections.emptyMap());

        byte[] output = testObject.getBytes();

        assertThat(output, isByteArrayIncludingSpacesFor("line1"));
    }

    @Test
    public void tagsRemovedWhenNoMatchingReplacementFound() throws IOException {
        String fileContents = "line1\nother content %WIBBLE% ";
        TemplatedFileToBytesConverter testObject = createTestObject(fileContents, Collections.emptyMap());

        byte[] output = testObject.getBytes();

        assertThat(output, isByteArrayIncludingSpacesFor("line1\nother content  "));
    }

    @Test
    public void tagsCanBeReplaced() throws IOException {
        String fileContents = "line1\n %WIBBLE% ";
        Map<String, String> tags = new HashMap<>();
        tags.put("WIBBLE", "fish");
        TemplatedFileToBytesConverter testObject = createTestObject(fileContents, tags);

        byte[] output = testObject.getBytes();

        assertThat(output, isByteArrayIncludingSpacesFor("line1\n fish "));
    }

    private TemplatedFileToBytesConverter createTestObject(String fileContents, Map<String, String> tags) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContents.getBytes());
        return new TemplatedFileToBytesConverter(inputStream, tags);
    }

}
