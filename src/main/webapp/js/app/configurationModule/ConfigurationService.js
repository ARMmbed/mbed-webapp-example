angular.module('App.services', ['ngResource']);

angular.module('App.services').factory('Subscriptions', function ($resource) {
    return $resource('webapi/subscriptions', {}, {
        update: {
            method: 'PUT'
        }
    });
});
angular.module('App.services').factory('Configuration', function ($resource) {
    return $resource('webapi/configuration', {}, {
        set: {
            method: 'POST'
        },
        get: {
            method: 'GET', isArray: false
        }
    });
});
angular.module('App.services').factory('ConnectionStatus', function ($http) {
    return {
        getStatus: function () {
            return $http.get('webapi/mbedclient');
        }
    };
});