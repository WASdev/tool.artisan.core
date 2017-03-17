/*******************************************************************************
 * Copyright (c) 2016,17 IBM Corp.
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
package com.ibm.liberty.starter.service.microprofile.api.v1.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ibm.liberty.starter.api.v1.model.provider.Dependency;
import com.ibm.liberty.starter.api.v1.model.provider.Provider;
import com.ibm.liberty.starter.api.v1.model.provider.Sample;

/**
 * Test the deployed service responds as expected
 *
 */
public class TestApplication extends EndpointTest {

    @Before
    public void checkSetup() {
        checkAvailability("/available/");
    }

    @Test
    public void testProvider() throws Exception {
        String ok = testEndpoint("/available/");
        assertNotNull("No response from API for provider", ok);
        assertTrue("OK was not found.", ok.contains("OK"));
    }
}
