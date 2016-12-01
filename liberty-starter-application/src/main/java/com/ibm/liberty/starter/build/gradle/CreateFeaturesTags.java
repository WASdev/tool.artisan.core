package com.ibm.liberty.starter.build.gradle;

import com.ibm.liberty.starter.build.FeaturesToInstallProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateFeaturesTags {
    private final List<String> featuresToInstall;

    public CreateFeaturesTags(FeaturesToInstallProvider featuresToInstallProvider) {
        featuresToInstall = featuresToInstallProvider.getFeatures();
    }

    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        addFeatureInstallConfig(tags);
        addTaskSetupTag(tags);
        return tags;
    }

    private void addFeatureInstallConfig(Map<String, String> tags) {
        tags.put("LIBERTY_FEATURE_CONFIG", "    features {\n" +
                        "        name = [" + featuresAsCommasSeparatedList() + "]\n" +
                        "        if (project.hasProperty('acceptFeaturesLicense') && acceptFeaturesLicense) {\n" +
                        "            acceptLicense = true\n" +
                        "        } else {\n" +
                        "            throw new GradleException(\"You must set a value for the 'acceptFeaturesLicense' property. Please review the license terms and conditions for additional features to be installed and if you accept the license terms and conditions then run the Gradle command with '-PacceptFeaturesLicense=true'.\")\n" +
                        "        }\n" +
                        "    }");
    }

    private String featuresAsCommasSeparatedList() {
        return featuresToInstall.stream().map(feature -> "'" + feature + "'").collect(Collectors.joining(", "));
    }

    private void addTaskSetupTag(Map<String, String> tags) {
        tags.put("LIBERTY_FEATURE_TASK_SETUP", "installFeature.dependsOn 'installLiberty'\n" +
                "setupServer.dependsOn 'installFeature'");
    }
}
