(function () {
var as = angular.module('eMatherApp.services', []);


as.factory('EMatcherService',
	    ['$localStorage','$http', '$q', 
	        function ($localStorage, $http, $q) {

	            var factory = {
	                loadAllCompaniesData: loadAllCompaniesData,
	                getAllCompaniesData: getAllCompaniesData
	            };

	            return factory;
	            
	            function loadAllCompaniesData() {
	            	alert("-----hello chiru----------");
	                console.log('Fetching all Companies Data');
	                var deferred = $q.defer();
	                var actionUrl = 'api/ematcher/getAllCountriesData';
	                $http.get(actionUrl)
	                    .then(
	                        function (response) {
	                            console.log('Fetched successfully all users');
	                            $localStorage.contriesData = response.data;
	                            deferred.resolve(response);
	                        },
	                        function (errResponse) {
	                            console.error('Error while loading users');
	                            deferred.reject(errResponse);
	                        }
	                    );
	                return deferred.promise;
	            }
	            
	            function getAllCompaniesData()
	            {
	            	 return $localStorage.contriesData;
	            }
	            
	        }
	    ]);
   
})();              