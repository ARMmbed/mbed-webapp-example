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
    function Ctrl($scope, $http, ConnectionStatus, Configuration, $location) {
        ConnectionStatus.getStatus().then(
            function (data) {
                $scope.isConnected = data.data == true;
                $scope.isDisonnected = data.data == true;
            }, function (data, status) {
                console.log('Error!!', status, data);
            });
        Configuration.get().$promise.then(function (data) {
            $scope.address = data.address;
            $scope.selection = data.token == null ? "userPass" : "token";
            $scope.data = data;
        }, function (data, status) {
            console.log('Error!!', status, data);
        });

    });
angular.module('App.controllers').controller('subCtrl', function ($scope, $filter, Subscriptions, $location, $rootScope) {
    $scope.notSaved = false;
    $scope.show_close = true;
    $scope.subscriptions = Subscriptions.query();
    $scope.btn_text = 'add';
    $rootScope.$on('$locationChangeStart', function (event) {
        if (tabName != 'subscription' && $scope.notSaved && !confirm("You have unsaved changes, do you want to continue?")) {
            event.preventDefault();
        }
        else {
            $(parent).tab('show');
        }
    });
    window.onbeforeunload = function () {
        if ($scope.notSaved) {
            return "You have unsaved changes, do you want to continue?";
        }
    };

    var parent;
    var tabName;
    $rootScope.tabclick = function (event, name) {
        parent = event.target;
        tabName = name;
    };

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
            $scope.notSaved = true;
            $scope.ok = null;
            $scope.error = null;
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
        $scope.notSaved = true;
        $scope.ok = null;
        $scope.error = null;
    };
    $scope.save = function () {
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
        Subscriptions.update({}, preSubscriptionList).$promise.then(function () {
            $scope.ok = "Saved successfully!";
        }, function (data) {
            console.log("error in pushing pre-subscription ", data.status, data.statusText);
            $scope.error = data.statusText;
        }).finally(function () {
            $scope.successfulPush = true;
        });
        $scope.refresh();
        $scope.notSaved = false;
    };
})
;
//angular.module('App.controllers').run(['$rootScope','$location', function ($rootScope,$location) {
//    //alert($scope.text);
//    //alert($scope.text);
//    var _preventNavigation = false;
//    var _preventNavigationUrl = null;
//
//    $rootScope.allowNavigation = function() {
//        _preventNavigation = false;
//    };
//
//    $rootScope.preventNavigation = function() {
//        _preventNavigation = true;
//        _preventNavigationUrl = $location.absUrl();
//    }
//
//    $rootScope.$on('$locationChangeStart', function (event, newUrl, oldUrl) {
//        // Allow navigation if our old url wasn't where we prevented navigation from
//        confirm("You have unsaved changes, do you want to continue?")
//        if (_preventNavigationUrl != oldUrl || _preventNavigationUrl == null) {
//            $rootScope.allowNavigation();
//            return;
//        }
//
//        if (_preventNavigation && !confirm("You have unsaved changes, do you want to continue?")) {
//            event.preventDefault();
//        }
//        else {
//            $rootScope.allowNavigation();
//        }
//    });
//
//    // Take care of preventing navigation out of our angular app
//    window.onbeforeunload = function() {
//        // Use the same data that we've set in our angular app
//        //if (_preventNavigation && $location.absUrl() == _preventNavigationUrl) {
//            return "You have unsaved changes, do you want to continue?";
//        //}
//    }
//
//
//}]);
