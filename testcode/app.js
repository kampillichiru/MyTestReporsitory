(function() {
	'use strict';

	var
	//the HTTP headers to be used by all requests
	httpHeaders,
	//the message to be shown to the user
	message,
	//Define the main module.
	//The module is accessible everywhere using "angular.module('angularspring')", therefore global variables can be avoided totally.
	as = angular.module(
			'eMatherApp',
			[ 'ngRoute', 'ngResource', 'ngCookies', 'ngSanitize',
					'ui.bootstrap', 'ngMessages', 'eMatherApp.translate',
					'eMatcherApp.templates', 'eMatherApp.i18n',
					'eMatherApp.services', 'eMatherApp.controllers',
					'eMatherApp.filters', 'eMatherApp.view1',
					'eMatherApp.view2', 'eMatherApp.version',
					'angucomplete-alt', 'ngStorage' ]).config(
			function($routeProvider, $httpProvider) {

				//configure the rounting of ng-view
				$routeProvider.when('/', {
					templateUrl : 'home.html',
					publicAccess : true,
					resolve : {
					}
				}).when('/home', {
					templateUrl : 'home.html',
					publicAccess : true
				}).when('/login', {
					templateUrl : 'login.html',
					publicAccess : true
				}).when('/ematchSearch', {
					controller : 'EMatchSearchController',
					templateUrl : 'EMatchSearch.html',
					publicAccess : true
				}).otherwise({
					redirectTo : '/view1'
				});
				;

				//configure $http to catch message responses and show them
				$httpProvider.interceptors.push(function($q) {
					var setMessage = function(response) {
						//if the response has a text and a type property, it is a message to be shown
						if (response.data.text && response.data.type) {
							message = {
								text : response.data.text,
								type : response.data.type,
								show : true
							};
						}
					};

					return {
						//this is called after each successful server request
						'response' : function(response) {
							// console.log('request:' + response);
							setMessage(response);
							return response || $q.when(response);
						},
						//this is called after each unsuccessful server request
						'responseError' : function(response) {
							//console.log('requestError:' + response);
							setMessage(response);
							return $q.reject(response);
						}

					};
				});

				$httpProvider.interceptors.push(function($rootScope, $q) {

					return {
						'request' : function(config) {
							// console.log('request:' + config);
							return config || $q.when(config);
						},
						'requestError' : function(rejection) {
							// console.log('requestError:' + rejection);
							return rejection;
						},
						//success -> don't intercept
						'response' : function(response) {
							// console.log('response:' + response);
							return response || $q.when(response);
						},
						//error -> if 401 save the request and broadcast an event
						'responseError' : function(response) {
							console.log('responseError:' + response);
							if (response.status === 401) {
								var deferred = $q.defer(), req = {
									config : response.config,
									deferred : deferred
								};
								$rootScope.requests401.push(req);
								$rootScope.$broadcast('event:loginRequired');
								return deferred.promise;
							}
							return $q.reject(response);
						}

					};
				});

				httpHeaders = $httpProvider.defaults.headers;
			}).run(
			function($rootScope, $http, $route, $location, EMatcherService,
					$localStorage) {
				//make current message accessible to root scope and therefore all scopes
				$rootScope.message = function() {
					return message;
				};

			});
})();