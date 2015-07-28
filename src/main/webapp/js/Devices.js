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

var app = angular.module('App', ['ngResource']).directive('ngResource', function() {
                                                 return function(scope, element, attrs) {
                                                   var button = angular.element(element);
                                                   $(button).popover({
                                                   template: '<div class="popover success_popover" role="tooltip" style="width: 500px;">' +
                                                        '<div class="arrow success_popover-arrow">' +
                                                        '</div>' +
                                                        '<h3 class="popover-title">' +
                                                        '</h3>' +
                                                        '<div class="popover-content success_popover-content">' +
                                                            '<div class="data-content">' +
                                                            '</div>' +
                                                        '</div>' +
                                                   '</div>',
                                                   content:""
                                                   });
                                                 };
                                               });
app.directive('ngAction', function() {
          return function(scope, element, attrs) {
            editable_clicked = angular.element(element);
            $(editable_clicked).editable({
                 title: 'ENTER YOUR COMMAND',
                 value: '',
                 pk:1,
                 type: 'textarea',
                 emptytext: 'action',
                 emptyclass: '',
                 rows: 5,
                 tpl: '<textarea id="commandValue"></textarea>'
             });
          };
        });

app.directive('ngConfirmClick', [
        function(){
            return {
                link: function (scope, element, attr) {
                    var msg = attr.ngConfirmClick || "Are you sure?";
                    var clickAction = attr.confirmedClick;
                    element.bind('click',function (event) {
                        if ( window.confirm(msg) ) {
                            scope.$eval(clickAction)
                        }
                    });
                }
            };
    }])

app.factory('Endpoints', function($resource) {
        return $resource('/example-app/webapi/endpoints/:endpoint_name/:url_path',{endpoint_name:'@endpoint_name',url_path:'@url_path'},{
         update:{
            method: 'PUT'
        },
        set:{
            method: 'POST'
        }
      });
    });
app.factory('GetValues', function($http,$q) {
                            return {
                                    getValues: function(endpoint_name) {
                                    var deferred = $q.defer();
                                    $http.get('/example-app/webapi/endpoints/'+endpoint_name+'/values')
                                        .success(function (data){
                                            deferred.resolve(data);
                                        });
                                    return deferred.promise;
                                    }
                                };

                        });

app.controller('Ctrl', function($scope, Endpoints, GetValues, $http,$element,$compile,$interval,$filter) {

    //modify buttons style
    var dynamical_buttons = $compile('<div class="btn-group-vertical">' +
                                     '<button ng-click = get($event,detail) type="button" class="btn btn-info">GET</button>'+
                                     '<button ng-click = post($event) type="button" class="btn btn-success">POST</button>'+
                                     '<button ng-click = put($event,detail,commandValue) type="button" class="btn btn-warning">PUT</button>'+
                                     '<button ng-confirm-click="Are you sure?" type="button" class="btn btn-danger">DELETE</button></div>')($scope);
    $.fn.editableform.buttons = dynamical_buttons;
    $scope.isHidden = true;
    $scope.isLoading = false;
    $scope.endpoints = Endpoints.query();
    $scope.detail = "Details"
    $scope.show_resources = function(name) {
        $scope.isHidden = false;
        $scope.panel_show = false;
        $scope.detail = name;
        $scope.endresources = Endpoints.query({'endpoint_name': name })
            .$promise.then(
               //success
               function( value ){
                    $scope.endresources = value;
                    },
               //error
               function( error ){
                   }
             );
    };
    $scope.get = function(event,name) {
        //$resource consider everything as an array which causes problem when the returning value type is a String, so $http is used.
        $(parent).editable('toggle');
        $http.get('/example-app/webapi/endpoints/'+name+'/request'+path
            ).success(function(data){
            selected_record.show = true;
        }).error(function(data, status) {
                             console.error('Repos error', status, data);
                           });;
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
    $scope.post = function(value){
            $(parent).editable('toggle');
    //        $http({
    //            url: '/example-app/webapi/endpoints/' + selected_name+'/' + selected_td.x.uri,
    //            method: "PUT",
    //            params: {'value': value}
    //         }).success(function(data){
    //                     selected_td.x.val = data;
    //               });
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
    var endpoint_name="none";
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
                                        d.val = value.value;
                                        d.show = false;
                                        d.lastUpdate = "Last update: " + $filter('date')(value.timestamp,"MM/dd/yyyy 'at' h:mma");
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