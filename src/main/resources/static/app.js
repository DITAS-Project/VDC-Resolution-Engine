var app = angular.module('app', []);

app.controller("searchController", function($scope, $http, $rootScope, parseService){
	
	$rootScope.search = false;
	$scope.search = function(){
		
		$http({
			method: 'POST',
			url: "/searchBlueprintByReq",     
			data: $scope.searchText,
			headers: {'Content-Type': 'application/json'}
		})
		.then(function successCallback(response) {
			$rootScope.search = true
			//console.log(response.data)
			parseService.parseResults(response.data)
		}, function errorCallback(response) {
			console.log(response)
		});
	}

});
