/*
 * Copyright (c) 2015, ARM Limited, All Rights Reserved
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
angular.module('App.controllers', []);
angular.module('App.controllers').controller('Ctrl', function($scope, Endpoints, GetValues, $http,$element,$compile,$interval,$filter,$location) {

    //modify buttons style
    var dynamical_buttons = $compile('<div class="btn-group-vertical">' +
                                     '<button ng-click = get($event,detail) type="button" class="btn btn-info">GET</button>'+
                                     '<button ng-click = post($event,detail) type="button" class="btn btn-success">POST</button>'+
                                     '<button ng-click = put($event,detail) type="button" class="btn btn-warning">PUT</button>'+
                                     '<button ng-click = delete($event,detail) ng-confirm-click="Are you sure?" type="button" class="btn btn-danger">DELETE</button></div>')($scope);
    $.fn.editableform.buttons = dynamical_buttons;
    $scope.isHidden = true;
    $scope.isLoading = false;
    //$scope.endpoints = Endpoints.query();
    var endpoint_name = $location.search().endpoint;
    $scope.detail = endpoint_name;
    $scope.endresources = Endpoints.query({'endpoint_name': endpoint_name })
        .$promise.then(
           //success
           function( value ){
                $scope.endresources = value;

                },
           //error
           function( error ){
               }
         );

    $scope.get = function(event,name) {
        //$resource consider everything as an array which causes problem when the returning value type is a String, so $http is used.
        $(parent).editable('toggle');
        $http.get('/example-app/webapi/endpoints/'+name+'/request'+path
            ).success(function(data){
            selected_record.show = true;
        }).error(function(data, status) {
                             console.error('Repos error', status, data);
                           });
    };
    $scope.put = function(event,name){
        var value = $('#commandValue')[0].value;
        $(parent).editable('toggle');
        $http({
            url: '/example-app/webapi/endpoints/' + name + path,
            method: "PUT",
            params: {'value': value}
         }).success(function(data){
                     console.log("done");
                     selected_record.show = true;
               }).error(function(data, status) {
                   console.error('Repos error', status, data);
                 });
    };
    $scope.post = function(event,name){
            var value = $('#commandValue')[0].value;
            $(parent).editable('toggle');
            $http({
                url: '/example-app/webapi/endpoints/' + name + path,
                method: "POST",
                params: {'value': value}
             }).success(function(data){
                         selected_record.show = true;
                   }).error(function(data, status) {
                       console.error('Repos error', status, data);
                     });
        };
    $scope.delete = function(event,name) {
        $(parent).editable('toggle');
        Endpoints.delete({ endpoint_name : name, url_path : path});

    };
     var parent;
     var path;
     var selected_record;
    $scope.action_clicked = function(event,selected_path,record){
        parent = event.target;
        path = selected_path;
        selected_record = record;
    };
    $scope.selectedIndex = -1;
    $scope.itemClicked = function ($index,name) {
        endpoint_name = name;
        $scope.selectedIndex = $index;
      }
    setInterval(function(){
    if(endpoint_name != "none")
    {
                GetValues.getValues(endpoint_name).then(
                    function(results){
                     angular.forEach(results, function (value,key) {
                        var single_object = $filter('filter')($scope.endresources, function (d)
                            {
                                    var splited_data = key.toString().split("'");
                                    var endpoint = splited_data[1];
                                    var path = splited_data[3];
                                    if(endpoint_name == endpoint && d.uriPath == path && !value.waitingForResponse)
                                    {
                                        if(value.statusCode == 200)
                                        {
                                            d.val = value.value;
                                            d.show = false;
                                            d.lastUpdate = "Last update: " + $filter('date')(value.timestamp,"MM/dd/yyyy HH:mm")
                                                 + "\nContent Type: "+ value.contentType
                                                 + "\nMaximum age: " + value.maxAge;
                                            d.success = true;
                                        }
                                        else
                                        {
                                            d.val = "Error";
                                            d.show = false;
                                            d.lastUpdate = "Error number " + value.statusCode + ": " + (value.errorMessage == null ? "" : value.errorMessage) ;
                                            d.success = false;
                                        }
                                    }
                            })[0];
                                        });

                    })
//                    .error(function(data, status) {
//                                          console.error('Repos error', status, data);
//                                        });

              }

    },1000);
});