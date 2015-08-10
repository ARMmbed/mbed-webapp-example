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

angular.module('App.services', ['ngResource']);
angular.module('App.services').factory('Endpoints', function ($resource) {
    return $resource('webapi/endpoints/:endpoint_name/:url_path', {
        endpoint_name: '@endpoint_name',
        url_path: '@url_path'
    }, {
        update: {
            method: 'PUT',
            headers: {'Content-Type': 'text/plain'}
        },
        set: {
            method: 'POST',
            headers: {'Content-Type': 'text/plain'}
        },
        remove: {
            method: 'DELETE'
        }
    });
});
angular.module('App.services').factory('GetValues', function ($http, $q) {
    return {
        getValues: function (endpoint_name) {
            var deferred = $q.defer();
            $http.get('webapi/endpoints/' + endpoint_name + '/values')
                .success(function (data) {
                    deferred.resolve(data);
                });
            return deferred.promise;
        }
    };
});
angular.module('App.services').factory('Configuration', function ($http) {
    return {
        getConfiguration: function () {
            return $http.get('webapi/configuration');
        }
    };
});
angular.module('App.services').factory('ConnectionStatus', function ($http) {
    return {
        getStatus: function () {
            return $http.get('webapi/mbedclient');
        }
    };
});
angular.module('App.services').factory('Request', function ($http) {
    return {
        sendRequest: function (name, path) {
            return $http.get('webapi/endpoints/' + name + '/request' + path);
        }
    };
});