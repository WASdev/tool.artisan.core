/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ibm.liberty.starter.build.gradle.unit;

import com.ibm.liberty.starter.build.gradle.CreateRepositoryTags;
import com.ibm.liberty.starter.build.gradle.CreateRuntimeTags;
import com.ibm.liberty.starter.unit.MockDependencyHandler;

import org.apache.maven.wagon.InputData;
import org.junit.Test;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CreateRuntimeTagsTest {

    @Test
    public void repositoryTagSetBetaFlag() throws URISyntaxException {
        CreateRuntimeTags testObject = new CreateRuntimeTags(true);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(1));
        assertThat(tags, hasEntry("RUNTIME_URL", "version = \"2017.+\""));
    }
    
    @Test
    public void repositoryTagNotSetBetaFlag() throws URISyntaxException {
        CreateRuntimeTags testObject = new CreateRuntimeTags(false);

        Map<String, String> tags = testObject.getTags();

        assertThat(tags.size(), is(1));
        assertThat(tags, hasEntry("RUNTIME_URL", "runtimeUrl = \"http://repo1.maven.org/maven2/com/ibm/websphere/appserver/runtime/wlp-webProfile7/17.0.0.1/wlp-webProfile7-17.0.0.1.zip\""));
    }

}
