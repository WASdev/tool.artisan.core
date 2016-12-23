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
package com.ibm.liberty.starter.build.gradle;

import java.util.HashMap;
import java.util.Map;

public class CreateArtifactConfigTags {
    
    private String artifactId;
    private String groupId;
    private final String defaultArtifactId = "test";
    private final String defaultGroupId = "liberty.gradle";
    
    public CreateArtifactConfigTags(String artifactId, String groupId) {
        this.artifactId = artifactId != null ? artifactId : defaultArtifactId;
        this.groupId = groupId != null ? groupId : defaultGroupId;
    }
    
    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        tags.put("GROUP_ID", groupId);
        tags.put("ARTIFACT_ID", artifactId);
        return tags;
    }

}
