package com.ibm.liberty.starter.build.gradle.unit;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.build.gradle.CreateAppNameTags;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CreateAppNameTagsTest {

    @Test
    public void appNameIsSet() throws ParserConfigurationException, URISyntaxException {
        String appName = "TestAppName";
        DependencyHandler fakeDependencyHandler = MockDependencyHandler.getDependencyHandlerWithName(appName);
        CreateAppNameTags testObject = new CreateAppNameTags(fakeDependencyHandler);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(1));
        assertThat(tags, hasEntry("APP_NAME", appName));
    }

    @Test
    public void appNameDefaultsToLibertyProject() throws Exception {
        String appName = "LibertyProject";
        DependencyHandler fakeDependencyHandler = MockDependencyHandler.getDefaultInstance();
        CreateAppNameTags testObject = new CreateAppNameTags(fakeDependencyHandler);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(1));
        assertThat(tags, hasEntry("APP_NAME", appName));
    }

}
