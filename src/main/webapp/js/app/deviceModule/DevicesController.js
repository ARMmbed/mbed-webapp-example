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
angular.module('App.controllers',[]);
angular.module('App.controllers').controller('Ctrl', function ($scope, Endpoints, ConnectionStatus, Configuration, $window,$location) {

    Endpoints.query().$promise.then(function (data) {
        $scope.noDevice = data.length == 0 ? true : false;
        $scope.endpoints = data;
    }, function (data, status) {
        console.log('Error!!', status, data);
    });
    ConnectionStatus.getStatus().then(
        function (data) {
            $scope.isConnected = data.data == true;
            $scope.isDisconnected = data.data == false;
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
    $scope.show_resources = function (name) {
        $window.open('Resources.html#/?endpoint=' + name, "_self");
    };
});
angular.module('App.controllers').filter('search', function () {
    return function (items, str) {
        if (str == '') return items;

        var filtered = [];
        var rgx = new RegExp(str, '');
        var rgxstart = new RegExp(': ?"' + str);

        angular.forEach(items, function (item) {
            var stringified = JSON.stringify(item);
            item.points = (stringified.match(rgx) || []).length;

            if (rgxstart.test(stringified)) item.points++;

            if (item.points > 0) filtered.push(item);
        });
        return filtered;
    }
});