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

angular.module('App.controllers', ['ngAnimate']);
angular.module('App.controllers').controller('ConfCtrl',
    function ConfCtrl($scope, $http, Configuration) {
        $scope.save = function () {
            $scope.error = null;
            $scope.ok = null;
            $scope.done = false;
            if ($scope.selection.valueOf() == "userPass") {
                $scope.data.token = null;
            }
            else {
                $scope.data.username = null;
                $scope.data.password = null;
            }
            Configuration.set({}, $scope.data).$promise.then(function () {
                $scope.ok = "Success!";
            }, function (data) {
                $scope.error = data.data;
            }).finally(function () {
                $scope.done = true;
            });
        };
    });
angular.module('App.controllers').controller('Ctrl',
    function Ctrl($scope, $http, ConnectionStatus, Configuration) {
        ConnectionStatus.getStatus().then(
            function (data) {
                $scope.isConnected = data.data == true;
                $scope.isDisonnected = data.data == true;
                console.log('data', data);
            }, function (data, status) {
                console.log('Error!!', status, data);
            });
        Configuration.get().$promise.then(function (data) {
            $scope.address = data.address;
            $scope.data = data;
        }, function (data, status) {
            console.log('Error!!', status, data);
        });
    });
angular.module('App.controllers').controller('subCtrl', function ($scope, $filter, Subscriptions) {
    $scope.show_close = true;
    $scope.subscriptions = Subscriptions.query();
    console.log($scope.subscriptions);
    $scope.btn_text = 'add';

    $scope.addRow = function () {
        var pre_subscription = {};
        pre_subscription['endpointName'] = ($scope.txt_name == undefined || $scope.txt_name == '') ? null : $scope.txt_name;
        pre_subscription['endpointType'] = ($scope.txt_type == undefined || $scope.txt_type == '') ? null : $scope.txt_type;
        pre_subscription['uriPathPatterns'] = ($scope.txt_path == undefined || $scope.txt_path == '') ? null : [$scope.txt_path];
        if (pre_subscription['endpointName'] != null || pre_subscription['endpointType'] != null || pre_subscription['uriPathPatterns'] != null) {
            if (update_index != -1) {
                $scope.subscriptions.splice(update_index, 1);
            }
            $scope.subscriptions.push(pre_subscription);
            $scope.refresh();
        }
    };
    var update_index = -1;
    $scope.update = function (endpointName, endpointType, uriPathPatterns, row_number) {
        $scope.refresh();
        $scope.txt_name = endpointName;
        $scope.txt_type = endpointType;
        $scope.txt_path = uriPathPatterns;

        update_index = row_number;
        $scope.show_close = false;
        $scope.btn_text = 'Update';
    };
    $scope.refresh = function () {
        $scope.txt_name = '';
        $scope.txt_type = '';
        $scope.txt_path = '';
        update_index = -1;
        $scope.show_close = true;
        $scope.btn_text = 'Add';
    };
    $scope.delete = function (row_number) {
        $scope.subscriptions.splice(row_number, 1);
        $scope.refresh();
    };
    $scope.push = function () {
        $scope.successfulPush = false;
        $scope.ok = null;
        $scope.error = null;
        var preSubscriptionList = [];
        $filter('filter')($scope.subscriptions, function (d) {
            var item = {
                "endpointName": d.endpointName,
                "endpointType": d.endpointType,
                "uriPathPatterns": d.uriPathPatterns
            };
            preSubscriptionList.push(item);
        });
        Subscriptions.update({}, preSubscriptionList).$promise.then(function (data) {
            //$scope.subscriptions = Subscriptions.query();
            $scope.ok = "Pushed successfully!";
        }, function (data, status) {
            console.log("error in pushing pre-subscription ", data.status, data.statusText);
            $scope.error = data.statusText;
        }).finally(function () {
            $scope.successfulPush = true;
        });
        $scope.refresh();
    };
});
