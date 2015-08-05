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

angular.module('App.controllers', ['angularMoment']);
angular.module('App.controllers').controller('Ctrl', function($scope, Endpoints, GetValues, $http,$element,$compile,$interval,$filter,$location) {

    //modify buttons style
    var dynamical_buttons = $compile('<div class="btn-group-vertical">' +
                                     '<button ng-click = post($event,detail) type="button" class="btn btn-success">&nbsp;&nbsp;POST&nbsp;&nbsp;</button>'+
                                     '<button ng-click = put($event,detail) type="button" class="btn btn-warning">PUT</button></div>'+
                                     '<div style="margin-top:25px;"><button ng-click = delete($event,detail) ng-confirm-click="Are you sure?" type="button" class="btn btn-danger">DELETE</button></div>')($scope);
    $.fn.editableform.buttons = dynamical_buttons;
    $scope.isHidden = true;
    $scope.isLoading = false;
    //$scope.endpoints = Endpoints.query();
     $scope.isConnected = false;
    $scope.isDisonnected = false;
    $http.get('webapi/mbedclient'
                    ).success(function(data){
                    $scope.isConnected  = data == "true";
                    $scope.isDisonnected  = data == "true";
                    console.log(data);
                }).error(function(data, status) {
                     console.error('error', status, data);
                   });
    $http.get('webapi/configuration').success(function (incoming) {
                   $scope.address = incoming.address;
               });
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
    var waiting_endpoint;
    var waiting_uri;
    $scope.get = function(event,name,path,selected_record) {
        //$resource consider everything as an array which causes problem when the returning value type is a String, so $http is used.
        $(parent).editable('toggle');
        $http.get('webapi/endpoints/' + name + '/request' + path
            ).success(function(data){
            selected_record.show = true;
            $scope.isDisabled = "true";
            waiting_endpoint = name;
            waiting_uri = path;
        }).error(function(data, status) {
                             console.error('Repos error', status, data);
                           });
    };
    $scope.put = function(event,name){
        var value = $('#commandValue')[0].value;
        $(parent).editable('toggle');
        $http({
            url: 'webapi/endpoints/' + name + path,
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
                url: 'webapi/endpoints/' + name + path,
                method: "POST",
                params: {'value': value}
             }).success(function(data){
                         selected_record.show = true;
                   }).error(function(data, status) {
                       console.error('Repos error', status, data);
                     });
//            Endpoints.set({params : value, endpoint_name : name, url_path : path}).$promise.then(function(data){
//                        selected_record.show = true;
//                    });
        };
    $scope.delete = function(event,name) {
        $(parent).editable('toggle');
        Endpoints.delete({ endpoint_name : name, url_path : path}).$promise.then(function(data){
            selected_record.show = true;
        });

    };
     var parent;
     var path;
     var selected_record;
    $scope.action_clicked = function(event,selected_path,record){
        parent = event.target;
        path = selected_path;
        selected_record = record;
    };
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
                                            d.lastUpdate = value.timestamp;
                                            d.content = "<br />Content Type: "+ value.contentType
                                                 + "<br />maxAge: " + value.maxAge;
                                            d.success = true;
                                        }
                                        else
                                        {
                                            d.val = "Error";
                                            d.lastUpdate = "Error number " + value.statusCode + ": " + (value.errorMessage == null ? "" : value.errorMessage) ;
                                            d.success = false;
                                        }
                                        d.show = false;
                                        if(waiting_endpoint == endpoint && waiting_uri == path)
                                        {
                                            $scope.isDisabled = "false";
                                        }
                                    }
                                    else if(endpoint_name == endpoint && d.uriPath == path && value.waitingForResponse)
                                    {
                                        d.show = true;
                                    }

                            })[0];
                                        });

                    },function(data, status) {
                                          console.log('Repos error', status, data);
                                        });

              }

    },1000);
});