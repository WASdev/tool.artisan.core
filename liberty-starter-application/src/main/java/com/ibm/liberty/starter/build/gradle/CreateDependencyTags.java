package com.ibm.liberty.starter.build.gradle;

import com.ibm.liberty.starter.DependencyHandler;
import com.ibm.liberty.starter.api.v1.model.provider.Dependency;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateDependencyTags {

    private DependencyHandler dependencyHandler;

    public CreateDependencyTags(DependencyHandler dependencyHandler) {
        this.dependencyHandler = dependencyHandler;
    }

    public Map<String,String> getTags() {
        StringBuilder dependencies = new StringBuilder();
        dependencies.append(createGradleDependencies("providedCompile", dependencyHandler.getProvidedDependency()));
        spaceOutDependencyGroup(dependencies, createGradleDependencies("runtime", dependencyHandler.getRuntimeDependency()));
        spaceOutDependencyGroup(dependencies, createGradleDependencies("compile", dependencyHandler.getCompileDependency()));

        return Collections.singletonMap("DEPENDENCIES", dependencies.toString());
    }

    private String createGradleDependencies(String scope, Map<String, Dependency> dependencies) {
        return dependencies.entrySet().stream()
                .map(entry -> createDependency(scope, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private String createDependency(String scope, String serviceId, Dependency dependency) {
        return "    " + scope + " 'net.wasdev.wlp.starters." + serviceId + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + "'";
    }

    private void spaceOutDependencyGroup(StringBuilder dependencies, String newGroup) {
        if (!newGroup.isEmpty() && dependencies.length() > 0) {
            dependencies.append("\n\n");
        }
        dependencies.append(newGroup);
    }
}
