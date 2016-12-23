/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
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

import java.util.Map;

import org.junit.Test;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.ibm.liberty.starter.build.gradle.CreateArtifactConfigTags;

public class CreateArtifactConfigTagsTest {

    @Test
    public void artifactIdSet() {
        CreateArtifactConfigTags testObject = new CreateArtifactConfigTags("testArtifactId", null);
        Map<String, String> tags = testObject.getTags();
        assertThat(tags.get("ARTIFACT_ID"), is("testArtifactId"));
    }
    
    @Test
    public void groupIdSet() {
        CreateArtifactConfigTags testObject = new CreateArtifactConfigTags(null, "group.test.id");
        Map<String, String> tags = testObject.getTags();
        assertThat(tags.get("GROUP_ID"), is("group.test.id"));
    }
    
    @Test
    public void defaultIfNull() {
        CreateArtifactConfigTags testObject = new CreateArtifactConfigTags(null, null);
        Map<String, String> tags = testObject.getTags();
        assertThat(tags.get("GROUP_ID"), is("liberty.gradle"));
        assertThat(tags.get("ARTIFACT_ID"), is("test"));
    }

}
