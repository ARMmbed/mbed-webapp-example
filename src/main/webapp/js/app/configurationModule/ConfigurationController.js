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
angular.module('App.controllers').controller('ConfCtrl',
        function ConfCtrl($scope, $http) {
            $scope.save = function () {
                $scope.error = null;
                $scope.ok = null;
                $http.post('webapi/configuration', $scope.data).then(
                        function success(response) {
                            $scope.ok = "Success!";
                        },
                        function error(response) {
                            $scope.error = response.data;
                        });
            };
            $http.get('webapi/configuration').success(function (incoming) {
                $scope.data = incoming;
            });
        });
angular.module('App.controllers').controller('Ctrl',
        function Ctrl($scope, $http) {
        $http.get('webapi/mbedclient'
                        ).success(function(data){
                        $scope.isConnected  = data == "true";
                        console.log(data);
                    }).error(function(data, status) {
                         console.error('error', status, data);
                       });
        $http.get('webapi/configuration').success(function (incoming) {
                        $scope.address = incoming.address;
                    });
        });

angular.module('App.controllers').controller('subCtrl', function($scope,Subscriptions) {
    $scope.show_close = true;
    $scope.subscriptions = Subscriptions.query();
    $scope.btn_text = 'add';
    $scope.states = ['One', 'Two'];
    $scope.endpoint_name = {
        state: $scope.states[0]
    };
    $scope.endpoint_type = {
        state: $scope.states[0]
    };
    $scope.endpoint_path = {
        state: $scope.states[0]
    };
    var i = 1;
    var j = 1;
    var k = 1;
    $scope.active = function() {
        $scope.endpoint_name.state = $scope.states[i % 2];
        i++;
    };
    $scope.active_type = function() {
        $scope.endpoint_type.state = $scope.states[j % 2];
        j++;
    };
    $scope.active_path = function() {
        $scope.endpoint_path.state = $scope.states[k % 2];
        k++;
    };
    $scope.addRow = function(){
        var pre_subscription = {};
        pre_subscription['endpointName'] = ($scope.txt_name == undefined || $scope.txt_name == '') ? null : $scope.txt_name;
        pre_subscription['endpointType'] = ($scope.txt_type == undefined || $scope.txt_type == '') ? null : $scope.txt_type;
        pre_subscription['uriPathPatterns'] = ($scope.txt_path == undefined || $scope.txt_path == '') ? null : $scope.txt_path;
        if(pre_subscription['endpointName'] != null || pre_subscription['endpointType'] != null || pre_subscription['uriPathPatterns'] != null)
        {
            if(update_index != -1)
            {
                $scope.subscriptions.splice( update_index, 1 );
            }
            $scope.subscriptions.push(pre_subscription);
            $scope.refresh();
        }
    };
    var update_index =-1;
    $scope.update = function(text,row_number){
        $scope.refresh();
        var pre_subscription = text.split(',');
        for(item in pre_subscription)
        {
            if(pre_subscription[item].startsWith("endpoint-name"))
                $scope.txt_name = pre_subscription[item].substring(14,pre_subscription[item].length);
            else if(pre_subscription[item].startsWith("endpoint-type"))
                $scope.txt_type= pre_subscription[item].substring(14,pre_subscription[item].length);
            else if(pre_subscription[item].startsWith("resource-path"))
                $scope.txt_path= pre_subscription[item].substring(14,pre_subscription[item].length);
            else // because it is possible that we have commas in the path too
            {
                $scope.txt_path += "," + pre_subscription[item];
            }
        }
        update_index = row_number;
        $scope.show_close = false;
        $scope.btn_text = 'Update';

    };
    $scope.refresh = function(){
        $scope.txt_name = '';
        $scope.txt_type = '';
        $scope.txt_path = '';
        update_index = -1;
        $scope.show_close = true;
        $scope.btn_text = 'Add';
    };
    $scope.delete = function(row_number){
        $scope.subscriptions.splice( row_number, 1 );
        $scope.refresh();
    };
    $scope.push = function(){
        Subscriptions.update({"endpointName":"kkS","endpointType":"Light"});
        $scope.subscriptions = Subscriptions.query();
        $scope.subscriptions.refresh();
        $scope.refresh();
    };
});
