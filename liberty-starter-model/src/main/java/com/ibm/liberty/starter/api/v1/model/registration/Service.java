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
package com.ibm.liberty.starter.api.v1.model.registration;

import io.swagger.annotations.ApiModelProperty;

public class Service {

    private String id;
    private String name;
    private String description;
    private String endpoint;
    private String configuration;
    private boolean options;
    private String image;
    private String imageSelected;

    @ApiModelProperty(value="Unique ID for this technology", required=true)
    public String getId() {
            return id;
    }
    public void setId(String id) {
            this.id = id;
    }

    @ApiModelProperty(value="Name of the technology (does not have to be unique, although it will be confusing if it isn't)", required=true)
    public String getName() {
            return name;
    }
    public void setName(String name) {
            this.name = name;
    }

    @ApiModelProperty(value="Description of the technology", required=true)
    public String getDescription() {
            return description;
    }
    public void setDescription(String description) {
            this.description = description;
    }

    @ApiModelProperty(value="The endpoint to access the service", required=true)
    public String getEndpoint() {
            return endpoint;
    }
    public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
    }

    @ApiModelProperty(value="Specifies whether this technology allows any configuration(s). Value could either be 'yes' or a list of other technologies, seperated by comma. If a list of technologies is specified then the configuration(s) will be enabled only if all technologies on the list are selected by user", required=false)
    public String getConfiguration() {
            return configuration;
    }
    public void setConfiguration(String configuration) {
            this.configuration = configuration;
    }

    @ApiModelProperty(value="Specifies whether this technology has any additional options that can be set by the user", required=false)
    public boolean getOptions() {
            return options;
    }
    public void setOptions(boolean options) {
            this.options = options;
    }

    @Override
    public String toString() {
        return "Service [id=" + id + ", endpoint=" + endpoint + "]";
    }
    
    @ApiModelProperty(value="Specifies an image to display on the technology button", required=false)
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    @ApiModelProperty(value="Specifies an image to display on the technology button when it is selected", required=false)
    public String getImageSelected() {
        return imageSelected;
    }
    
    public void setImageSelected(String image) {
        this.imageSelected = image;
    }
}
