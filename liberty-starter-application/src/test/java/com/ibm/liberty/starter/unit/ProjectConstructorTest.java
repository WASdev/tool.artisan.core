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
package com.ibm.liberty.starter.unit;

import com.ibm.liberty.starter.ProjectConstructionInputData;
import com.ibm.liberty.starter.ProjectConstructor;
import com.ibm.liberty.starter.api.v1.model.internal.Services;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ProjectConstructorTest {

    @Test
    public void testMapInitializer() throws IOException {
        Services services = new Services();
        ProjectConstructor zipConstructor = new ProjectConstructor(new ProjectConstructionInputData(services, null, null, null, null, null, null, null, false));
        zipConstructor.initializeMap();
        Map<String, byte[]> map = zipConstructor.getFileMap();
        assertThat(map, is(not(anEmptyMap())));
        assertThat(map, hasKey("src/main/liberty/config/server.xml"));
        byte[] byteArray = map.get("src/main/liberty/config/server.xml");
        assertThat(byteArray.length, is(greaterThan(0)));
    }

}
