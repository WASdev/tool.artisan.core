package com.ibm.liberty.starter.build.gradle;

import com.ibm.liberty.starter.DependencyHandler;

import java.util.Collections;
import java.util.Map;

public class CreateAppNameTags {
    private final String appName;
    public CreateAppNameTags(DependencyHandler dependencyHandler) {
        appName = dependencyHandler.getAppName();
    }
    public Map<String, String> getTags() {
        return Collections.singletonMap("APP_NAME", appName);
    }
}
