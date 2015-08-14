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
angular.module('App.controllers').controller('Ctrl', function ($scope, Request, Endpoints, GetValues, ConnectionStatus
    , Configuration, $compile, $interval, $filter, $location) {

    //modify buttons style
    var dynamical_buttons = $compile('<div class="btn-group-vertical">' +
        '<button ng-click = post($event,detail) type="button" class="btn btn-success">&nbsp;&nbsp;POST&nbsp;&nbsp;</button>' +
        '<button ng-click = put($event,detail) type="button" class="btn btn-warning">PUT</button></div>' +
        '<div style="margin-top:25px;"><button ng-click = delete($event,detail) ng-confirm-click="Are you sure?" type="button" class="btn btn-danger">DELETE</button></div>')($scope);
    $.fn.editableform.buttons = dynamical_buttons;
    $scope.isHidden = true;
    $scope.isLoading = false;
    $scope.isConnected = false;
    $scope.isDisonnected = false;

    ConnectionStatus.getStatus().then(
        function (data) {
            $scope.isConnected = data.data == true;
            $scope.isDisonnected = data.data == true;
            console.log('data', data);
        }, function (data, status) {
            console.log('Error!!', status, data);
        });
    Configuration.getConfiguration().then(
        function (data) {
            $scope.address = data.data.address;
        }, function (data, status) {
            console.log('Error!!', status, data);
        });
    var endpoint_name = $location.search().endpoint;
    $scope.detail = endpoint_name;
    $scope.endresources = Endpoints.query({'endpoint_name': endpoint_name})
        .$promise.then(function (value) {
            $scope.endresources = value;
            console.log($scope.endresources);
        }, function (data, status) {
            console.error('Error!!', status, data);
        });
    var waiting_endpoint;
    var waiting_uri;
    $scope.get = function (event, name, path, selected_record) {
        $(parent).editable('toggle');
        Request.sendRequest(name, path).then(function (data) {
            selected_record.show = true;
            $scope.isDisabled = "true";
            waiting_endpoint = name;
            waiting_uri = path;
        }, function (data, status) {
            console.error('Error!!', status, data);
            selected_record.show = false;
        });
    };
    $scope.put = function (event, name) {
        var value = $('#commandValue')[0].value;
        $(parent).editable('toggle');
        Endpoints.update({endpoint_name: name, url_path: path}, value).$promise.then(function (data) {
            selected_record.show = true;
        }, function (data, status) {
            console.error('Error!!', status, data);
            selected_record.show = false;
        });
    };
    $scope.post = function (event, name) {
        var value = $('#commandValue')[0].value;
        $(parent).editable('toggle');
        Endpoints.set({endpoint_name: name, url_path: path}, value).$promise.then(function (data) {
            selected_record.show = true;
        }, function (data, status) {
            console.error('Error!!', status, data);
            selected_record.show = false;
        });
    };
    $scope.delete = function (event, name) {
        $(parent).editable('toggle');
        Endpoints.remove({endpoint_name: name, url_path: path}).$promise.then(function (data) {
            selected_record.show = true;
        }, function (data, status) {
            console.error('Error!!', status, data);
            selected_record.show = false;
        });
    };
    var parent;
    var path;
    var selected_record;
    /*The popup does not have access to path and selected row so we need to keep them in vars.*/
    $scope.action_clicked = function (event, selected_path, record) {
        parent = event.target;
        path = selected_path;
        selected_record = record;
    };
    setInterval(function () {
        if (endpoint_name != "none") {
            GetValues.getValues(endpoint_name).then(
                function (results) {
                    angular.forEach(results, function (value, key) {
                        var single_object = $filter('filter')($scope.endresources, function (d) {
                            var splited_data = key.toString().split("'");
                            var endpoint = splited_data[1];
                            var path = splited_data[3];
                            if (endpoint_name == endpoint && d.uriPath == path && !value.waitingForResponse) {
                                if (value.statusCode == 200) {
                                    d.val = value.value;
                                    d.lastUpdate = value.timestamp;
                                    d.content = "<br />Content Type: " + value.contentType
                                        + "<br />maxAge: " + value.maxAge;
                                    d.success = true;
                                }
                                else {
                                    d.val = "Error";
                                    d.lastUpdate = "Error number " + value.statusCode + ": " + (value.errorMessage == null ? "" : value.errorMessage);
                                    d.success = false;
                                }
                                d.show = false;
                                d.notification = value.notification;
                                if (waiting_endpoint == endpoint && waiting_uri == path) {
                                    $scope.isDisabled = "false";
                                }
                            }
                            else if (endpoint_name == endpoint && d.uriPath == path && value.waitingForResponse) {
                                d.show = true;
                            }

                        })[0];
                    });

                }, function (data, status) {
                    console.log('Error!!', status, data);
                });
        }
    }, 1000);
});