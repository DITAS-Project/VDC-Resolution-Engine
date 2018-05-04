var app = angular.module('app', []);

app.controller("searchController", function($scope, $http, $rootScope, parseService){
	
	$rootScope.search = false;
	$scope.search = function(){
		
		$http({
			method: 'POST',
			url: "/searchBP",     
			data: $scope.searchText,
			headers: {'Content-Type': 'application/json'}
		})
		.then(function successCallback(response) {
			$rootScope.search = true
			parseService.parseResults(response.data)
		}, function errorCallback(response) {
			console.log(response)
		});
	}

});
