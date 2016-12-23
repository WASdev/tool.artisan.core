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
package com.ibm.liberty.starter;

import java.util.regex.Pattern;

public class PatternValidation {

    public enum PatternType {
        TECH, NAME, PATH_EXTENSION, ARTIFACT_ID
    }

    public static boolean checkPattern(PatternType patternType, String object) {
        boolean patternPassed = false;
        Pattern pattern = getPattern(patternType);
        if (pattern.matcher(object).matches()) {
            patternPassed = true;
        }
        return patternPassed;
    }

    private static Pattern getPattern(PatternType patternType) {
        Pattern pattern = Pattern.compile("");
        switch (patternType) {
            case TECH:
                pattern = Pattern.compile("[a-z]*");
                break;
            case NAME:
                pattern = Pattern.compile("[a-zA-Z0-9_-]*");
                break;
            case PATH_EXTENSION:
                pattern = Pattern.compile("[a-zA-Z0-9-_/.]*");
                break;
            case ARTIFACT_ID:
                pattern = Pattern.compile("[a-zA-Z0-9-_.]*");
                break;
        }
        return pattern;
    }

}
