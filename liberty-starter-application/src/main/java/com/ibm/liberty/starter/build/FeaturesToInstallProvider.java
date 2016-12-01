package com.ibm.liberty.starter.build;

import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FeaturesToInstallProvider {

    private final List<String> listOfFeatures = new ArrayList<>();
    private static final Logger log = Logger.getLogger(FeaturesToInstallProvider.class.getName());

    public FeaturesToInstallProvider(Services services, ServiceConnector serviceConnector) {
        for (Service service : services.getServices()) {
            String features = serviceConnector.getFeaturesToInstall(service);
            if (features != null && !features.trim().isEmpty() && (features.split(",").length > 0)) {
                for (String feature : features.split(",")) {
                    if (!listOfFeatures.contains(feature)) {
                        listOfFeatures.add(feature);
                        log.finer("Added feature : " + feature);
                    }
                }
            }
        }
    }

    public List<String> getFeatures() {
        return listOfFeatures;
    }
}
