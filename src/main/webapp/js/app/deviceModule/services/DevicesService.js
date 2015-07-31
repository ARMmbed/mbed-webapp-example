angular.module('App.services', ['ngResource']);
angular.module('App.services').factory('Endpoints', function($resource) {
        return $resource('/example-app/webapi/endpoints/:endpoint_name/:url_path',{endpoint_name:'@endpoint_name',url_path:'@url_path'});
    });