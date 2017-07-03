(function () {
    var as = angular.module('eMatherApp.controllers', []);

    as.controller('MainController', function ($q, $scope, $rootScope, $http, $translate, $location,EMatcherService,$localStorage) {


		$localStorage.contriesData= [{
			  "code" : "6410",
			  "name" : "AllianceBernstein (Seattle)"
			}, {
			  "code" : "7992",
			  "name" : "Avis Budget Car Rental"
			}, {
			  "code" : "9205",
			  "name" : "Dolphin Enterprise Solutions Corp"
			}, {
			  "code" : "7453",
			  "name" : "NVIDIA - Westford MA"
			}, {
			  "code" : "6721",
			  "name" : "Justerini & Brooks Wines"
			}, {
			  "code" : "7759",
			  "name" : "Hecht's"
			}, {
			  "code" : "6560",
			  "name" : "Groesbeck Plant (TX)"
			}, {
			  "code" : "8673",
			  "name" : "Doo Dads "
			}, {
			  "code" : "7601",
			  "name" : "KPNX-TV (Phoenix, Arizona)"
			}, {
			  "code" : "7141",
			  "name" : "Trinity Parts & Components"
			}, {
			  "code" : "8873",
			  "name" : "Birkman International Inc."
			}, {
			  "code" : "9528",
			  "name" : "KVOO  FM  Tulsa OK"
			}];

        
        $scope.countries = EMatcherService.getAllCompaniesData();
        $scope.countrySelected9 = {
				name : '',
				code : ''
			};
		$scope.countrySelectedFn9 = function(selected) {
			if (selected) {
				$scope.countrySelected9 = selected.originalObject;
				$rootScope.companyName=$scope.countrySelected9.name;
				$rootScope.companyCode=$scope.countrySelected9.code;
				 var path=$location.path();
				  if ($scope.countrySelected9.name != '') {  
					  $scope.$broadcast('angucomplete-alt:clearInput');
					$location.url('/ematchSearch');
	                }
				 
			} else {
				$scope.countrySelected9 = null;
			}
		}
		
		$scope.filterFunction = function(str, countries) {

		    // Change this value to increase total items users see
		    var maxLength = 100;

		    var i, matches = []; // This array contains what the user sees


		    var strLen = str.length; // I used this to ensure exact matching
		    if(strLen<=2)
		    {
		    	maxLength=19;
		    }
		    str = str.toLowerCase(); // Use to make case insensitive

		    // Iterate through all cName points
		    for (i = 0; i < countries.length; i++) {
		      var cName = countries[i]

		      // Choose parameters we want user to be able to filter
		      var name = cName.name.toLowerCase().substring(0, strLen);

		        // Check for matches and if the list is larger than max elements
		        // terminate function
		        if (name.indexOf(str, 0) !== -1) {
		            matches.push(cName);
		            if (matches.length > maxLength) {
		        	    console.log('matches143====>'+matches.length);
		              return matches;
		            }
		        }

		    }

		    // What is displayed to users
		    console.log('matches====>'+matches.length);
		    return matches;
		};

        $scope.language = function () {
            return $translate.use();
        };

        $scope.setLanguage = function (lang) {
            $translate.use(lang);
        };

        $scope.activeWhen = function (value) {
            return value ? 'active' : '';
        };

        $scope.path = function () {
            return $location.url();
        };


    });


    as.controller('EMatchSearchController', function ($scope, $rootScope, $http,  $location,EMatcherService) {
        $scope.data = {};
        var actionUrl = 'api/ematcher/getCompanyDetails/'+$rootScope.companyName+'/'+$rootScope.companyCode;
        console.log('$rootScope.companyName'+$rootScope.companyName);
        console.log('$rootScope.companyCode'+$rootScope.companyCode);
        $http.get(actionUrl)
        .success(function (response) {
           $scope.donationDetails = response;
        })
        .error(function (data) {
            console.log(data);
        });

        $scope.doTheBack = function () {
            $location.url('/home');
        };

    });
    


}());