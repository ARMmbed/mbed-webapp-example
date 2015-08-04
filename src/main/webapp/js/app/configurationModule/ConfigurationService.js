angular.module('App.services', ['ngResource']);

angular.module('App.services').factory('Subscriptions', function($resource) {
          return $resource('webapi/subscriptions',{},{
              update:{
            method: 'PUT'
        }
    });
});