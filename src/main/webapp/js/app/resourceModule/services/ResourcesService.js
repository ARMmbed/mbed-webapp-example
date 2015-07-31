angular.module('App.services', ['ngResource']);
angular.module('App.services').factory('Endpoints', function($resource) {
        return $resource('/example-app/webapi/endpoints/:endpoint_name/:url_path',{endpoint_name:'@endpoint_name',url_path:'@url_path'},{
         update:{
            method: 'PUT'
        },
        set:{
            method: 'POST'
        },
        delete:{
                    method: 'DELETE'
                }
      });
    });
angular.module('App.services').factory('GetValues', function($http,$q) {
                            return {
                                    getValues: function(endpoint_name) {
                                    var deferred = $q.defer();
                                    $http.get('/example-app/webapi/endpoints/'+endpoint_name+'/values')
                                        .success(function (data){
                                            deferred.resolve(data);
                                        });
                                    return deferred.promise;
                                    }
                                };
                        });