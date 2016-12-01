package com.ibm.liberty.starter.build.gradle.unit;

import com.ibm.liberty.starter.build.gradle.CreateRepositoryTags;
import com.ibm.liberty.starter.unit.MockDependencyHandler;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CreateRepositoryTagsTest {

    @Test
    public void repositoryTagSetToSuppliedLocation() throws URISyntaxException {
        CreateRepositoryTags testObject = new CreateRepositoryTags(MockDependencyHandler.getDefaultInstance());

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(1));
        assertThat(tags, hasEntry("REPOSITORY_URL", "http://mock/start/api/v1/repo"));
    }

}
