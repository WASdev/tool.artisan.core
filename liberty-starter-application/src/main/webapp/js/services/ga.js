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

//service for Google analytics

'use strict';

angular.module('appAccelerator')
   .factory('ga',
    [      '$log',
  function ($log) {
     $log.debug("GA Svc :Initialising Google analytics service");

     //only turn on analytics for live site
     var googleAnalytics = window.location.hostname == 'liberty-starter.wasdev.developer.ibm.com';
     $log.debug("GA Svc : Google Analytics is currently set to  : " + googleAnalytics);

     (function(i, s, o, g, r, a, m) {
         i['GoogleAnalyticsObject'] = r;
         i[r] = i[r] || function() {
             (i[r].q = i[r].q || []).push(arguments)
         }, i[r].l = 1 * new Date();
         a = s.createElement(o), m = s.getElementsByTagName(o)[0];
         a.async = 1;
         a.src = g;
         m.parentNode.insertBefore(a, m)
     })(window, document, 'script', '//www.google-analytics.com/analytics.js',
             'ga');

     var report = function(p1, p2, p3, p4, p5) {
        if (googleAnalytics) {
            ga(p1, p2, p3, p4, p5);
        } else {
            $log.debug("GA Svc: Logging request for ga(%o %o %o %o %o)", p1, p2, p3, p4, p5);
        }
     }

     if (googleAnalytics) {
         ga('create', 'UA-70962553-1', 'auto');
         ga('send', 'pageview');
     }

     return {
         report : report
     }
    }]);
