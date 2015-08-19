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
angular.module('App.directives', []);
angular.module('App.directives').directive('strongSecret', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attr, ctrl) {
            scope.$watch(attr.ngModel, function (v, p, scope) {
                if (v != null && v.length != 0) {
                    if (v.lastIndexOf('/', 0) === 0) {
                        scope.valid = true;
                        scope.message = null;
                    }
                    else {
                        scope.valid = false;
                        scope.message = 'The resource path should start with "/"';
                    }
                }
                else {
                    scope.valid = null;
                    scope.message = null;
                }
            });
        }
    };
});