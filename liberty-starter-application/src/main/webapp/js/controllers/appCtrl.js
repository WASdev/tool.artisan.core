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

'use strict';

angular.module('appAccelerator')
.controller('appCtrl', ['$scope', '$log', '$timeout', 'appacc', '$window', 'ga',
                function($scope,   $log,   $timeout,   appacc,   $window,   ga) {

  $log.debug("AppAccelerator : using controller 'appCtrl'");

  $scope.colCount = 3;    //used by angular to layout rows correctly
  $scope.hasTechnologies = false;
  $scope.serverError = false;
  $scope.technologies = [];   //this controls the layout of the technologies, the service has the list of selected ones
  $scope.states = ['SELECTED TECHNOLOGIES', 'DOWNLOAD'];
  $scope.state = 0;         //the state of the application e.g. have you selected a technology, not what step you are on
  $scope.step = 1;   //the current step that is being configured by the user
  $scope.maxSteps = 2;
  $scope.selectedCount = 0;
  $scope.deploy = {bluemix : appacc.deployToBluemix(),
    name : "LibertyProject",
    buildType : appacc.updateBuildType(),
    artifactid : null,
    groupid : null,
    beta : appacc.isBeta()
  };
  $scope.showConfigOptions = false;
  $scope.angleIconDown = "fa-angle-down";
  $scope.angleIconUp = "fa-angle-up";

  $scope.createDownloadUrl = function() {
    return appacc.createDownloadUrl();
  }

  $scope.createGitHubUrl = function() {
    return appacc.createGitHubUrl();
  }

  $scope.sendGAEvent = function(p1, p2, p3) {
	  ga.report('send', 'event', p1, p2, p3);
  }

  $scope.toggleSelected = function(technology, $event) {
    $event.stopPropagation();
    technology.selected = !technology.selected;
    var googleEventType = (technology.selected) ? "selected" : "deselected";
    $scope.sendGAEvent('Technology', googleEventType, technology.id);
    (technology.selected) ? appacc.addSelectedTechnology(technology.id) : appacc.removeSelectedTechnology(technology.id);
    $scope.selectedCount = appacc.getSelectedCount();
    $log.debug("AppAccelerator : Selected count " + $scope.selectedCount);
    technology.panel = technology.selected ? "panel-selected" : "panel-primary";
    technology.iconstyle = technology.selected ? "icon-selected" : "icon";
    $scope.updateService();
  }

  $scope.toggleInfo = function(technology, $event) {
    $event.stopPropagation();
    technology.info = !technology.info; //toggle selection
    technology.displayOptions = false;
  }

  $scope.showOptions = function(technology, $event) {
    $event.stopPropagation();
    technology.info = false;
    technology.displayOptions = !technology.displayOptions;
  }
  
  $scope.toggleConfigOptions = function($event) {
    $event.stopPropagation();
    $scope.showConfigOptions = !$scope.showConfigOptions;
  }

  //get a technology for specific ID
  $scope.getTechnology = function(id) {
    $log.debug("AppAccelerator : looking for technology with ID : " + id)
    for(var i = 0; i < $scope.technologies.length; i++) {
      var row = $scope.technologies[i];
      for(var j = 0; j < row.length; j++) {
        var technology = row[j];
        if(technology.id == id) {
          return technology;
        }
      }
    }
    return undefined;
  }

  $scope.updateService = function() {
    appacc.deployToBluemix($scope.deploy.bluemix);
    $log.debug("AppAccelerator : DeployToBluemix has value:" + appacc.deployToBluemix());
    appacc.updateName($scope.deploy.name);
    appacc.updateArtifactId($scope.deploy.artifactid);
    appacc.updateGroupId($scope.deploy.groupid);
    $log.debug("Updating build type to " + $scope.deploy.buildType);
    appacc.updateBuildType($scope.deploy.buildType);
    appacc.isBeta($scope.deploy.beta);
    $log.debug("AppAccelerator : beta has value: " + appacc.isBeta());
    appacc.notifyListeners();
  }

  $scope.getNavClass = function() {
    return ($scope.selectedCount) ? "navEnabled" : "navDisabled";
  }

  //advance to the next state, will also move to the next step
  $scope.nextState = function() {
    if($scope.state != $scope.states.length) {
      $scope.state++;
    }
  }

  //go back to the previous step
  $scope.prevStep = function() {
  }

  //return a list of the selected technologies
  $scope.getSelectedTechnologies = function() {
    var selected = "";
    for(var i = 0; i < $scope.technologies.length; i++) {
      var row = $scope.technologies[i];
      for(var j = 0; j < row.length; j++) {
        var technology = row[j];
        if(technology.selected) {
          selected += (" " + technology.name + ",");
        }
      }
    }
    if(selected.length > 0) {
      return selected.substring(0, selected.length - 1);
    }
    return selected;
  }
  
  function newRowNeeded(index) {
    return (index % $scope.colCount) === 0;
  }

  $scope.buildType = appacc.buildType;

  this.getTech = function() {
    appacc.getTechnologies().then(function(response) {
      //split the returned technologies into rows of X elements
      var row = undefined;
      for(var i = 0; i < response.length; i++) {
        if(newRowNeeded(i)) {
          row = [];
          $scope.technologies.push(row);
        }
        var technology = response[i];       //just make the code a bit more readable
        technology.selected = false;        //flag to indicate if user has selected this technology
        technology.info = false;            //do not show the information for this technology
        technology.displayOptions = false;  //don't show any options
        technology.panel = "panel-primary";
        technology.name = technology.name.substring(0,27); //Ensure that panels don't overflow
        technology.iconstyle = "icon";
        row.push(technology);
      }
      $scope.hasTechnologies = true;
      $log.debug('AppAccelerator : getTechnologies %o', $scope.technologies);
    }, function(error) {
      //error, so mark call as complete but show warning to user
      $scope.serverError = true;
      $scope.hasTechnologies = true;
    });
  };

//
  //trigger an initial population of technologies
  this.getTech();
}]);
