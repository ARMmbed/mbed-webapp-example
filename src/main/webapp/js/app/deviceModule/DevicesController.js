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

    $scope.endpoints = Endpoints.query();
    $scope.isConnected = false;
    $scope.isDisonnected = false;
    $scope.anyDevice = "true";//!$scope.endpoints.length;

    $scope.$on('$locationChangeStart ', function (event) {
        //event.preventDefault();
        alert(9);
        //if (vm.myForm!= null && vm.myForm.$dirty) {
            if (!confirm("Are you sure you want to leave this page?")) {
                event.preventDefault();
            }
        //}
         });
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
    $scope.show_resources = function (name) {
        $window.open('Resources.html#/?endpoint=' + name, "_self");
    };
});
//angular.module('App.controllers').run(['$rootScope', function ($rootScope,$scope) {
//    $rootScope.$on('$locationChangeStart', function (event,newUrl,oldUrl) {
//            alert(88);
//            console.log(newUrl); // http://localhost:3000/#/articles/new
//            console.log(oldUrl); // http://localhost:3000/#/articles
//            event.preventDefault(); // This prevents the navigation from happening
//        }
//    );
//
//
//
//}]);