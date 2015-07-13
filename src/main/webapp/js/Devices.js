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
                                                   '</div>'
                                                   });
                                                 };
                                               });
var editable_clicked;
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
                 rows: 5
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


app.controller('Ctrl', function($scope, Endpoints,$http,$element,$compile) {

    //modify buttons style
    var dynamical_buttons = $compile('<div class="btn-group-vertical">' +
                                     '<button ng-click = get($event) type="button" class="btn btn-info">GET</button>'+
                                     '<button ng-click = post($event) type="button" class="btn btn-success">POST</button>'+
                                     '<button ng-click = put($event) type="button" class="btn btn-warning">PUT</button>'+
                                     '<button ng-confirm-click="Are you sure?" type="button" class="btn btn-danger">DELETE</button></div>')($scope);
    $.fn.editableform.buttons = dynamical_buttons;
    $scope.isHidden = true;
    $scope.hide_write = true;
    $scope.endpoints = Endpoints.query();

    $scope.show_resources = function(name) {
        $scope.isHidden = false;
        $scope.panel_show = false;
        $scope.hide_write = true;
        $scope.device_resource = name;
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
    var selected_td;
    var selected_name;
    $scope.show_write = function(name,path,td) {
        selected_td = td;
        selected_name = name;
        $scope.hide_write = false;
        $scope.Resource = path;
    };

    $scope.get = function(event,name,path,td) {
        //$resource consider everything as an array which causes problem when the returning value type is a String, so we used $http.
        $(parent).editable('toggle');
//        $http.get('/example-app/webapi/endpoints/'+name+'/'+path
//            ).success(function(data){
//          td.x.val = data;
//          loader = angular.element(loader);
//          loader.class = "";
//        });
    };
    $scope.put = function(value){
        $(parent).editable('toggle');
//        $http({
//            url: '/example-app/webapi/endpoints/' + selected_name+'/' + selected_td.x.uri,
//            method: "PUT",
//            params: {'value': value}
//         }).success(function(data){
//                     selected_td.x.val = data;
//               });
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
    $scope.close = function(){
            $scope.hide_write = true;
            $scope.write_text = "";
        };
     var parent;
    $scope.action_clicked = function(event){
        parent = event.target;

    };
});