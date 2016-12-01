package com.ibm.liberty.starter.build.unit;

import com.ibm.liberty.starter.ServiceConnector;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import com.ibm.liberty.starter.api.v1.model.registration.Service;
import com.ibm.liberty.starter.build.FeaturesToInstallProvider;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class FeaturesToInstallProviderTest {

    @Test
    public void obtainsFeaturesFromServices() throws URISyntaxException {
        FeaturesToInstallProvider testObject = createFeaturesToInstallProviderTestObject();

        List<String> features = testObject.getFeatures();

        assertThat(features, hasSize(1));
        assertThat(features, contains("Wibble"));
    }

    public static FeaturesToInstallProvider createFeaturesToInstallProviderTestObject() throws URISyntaxException {
        Services services = new Services();
        services.setServices(Collections.singletonList(new Service()));
        final String fakeFeatureName = "Wibble";
        FeaturesToInstallProvider testObject = new FeaturesToInstallProvider(services, new ServiceConnector(new URI("")) {
            @Override
            public String getFeaturesToInstall(Service service) {
                return fakeFeatureName;
            }

            @Override
            public Services parseServicesJson() {
                return services;
            }
        });
        return testObject;
    }

}
