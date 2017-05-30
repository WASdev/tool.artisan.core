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
package com.ibm.liberty.starter.build.gradle;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateRuntimeTags {
    private final String runtimeUrl;
    private Logger log = Logger.getLogger(CreateRuntimeTags.class.getName());
    private final static String betaURL = "version = \"2017.+\"";
    private final static String GAURL = "runtimeUrl = \"http://repo1.maven.org/maven2/com/ibm/websphere/appserver/runtime/wlp-webProfile7/17.0.0.1/wlp-webProfile7-17.0.0.1.zip\"";
    
    public CreateRuntimeTags(boolean beta) {
    	log.log(Level.INFO, "Use beta is " + beta);
    	if (beta) {
    		runtimeUrl =  betaURL;
    	} else {
    		runtimeUrl = GAURL;
    	}
    }
    
    public Map<String, String> getTags() {
        log.log(Level.INFO, "runtimeUrl is " + runtimeUrl);
        return Collections.singletonMap("RUNTIME_URL", runtimeUrl);
    }
}
