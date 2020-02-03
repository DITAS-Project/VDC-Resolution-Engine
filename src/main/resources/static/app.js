/*
 * This file is part of VDC-Resolution-Engine.
 * 
 * VDC-Resolution-Engine is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation, either version 3 of the License, 
 * or (at your option) any later version.
 * 
 * VDC-Resolution-Engine is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VDC-Resolution-Engine.  
 * If not, see <https://www.gnu.org/licenses/>.
 * 
 * VDC-Resolution-Engine is being developed for the
 * DITAS Project: https://www.ditas-project.eu/
 */
var app = angular.module('app', []);

app.controller("searchController", function($scope, $http, $rootScope, parseService){
	
	$rootScope.search = false;
	$scope.search = function(){
		//method to get blueprints
		$http({
			method: 'POST',
			url: "/searchBlueprintByReq",
			data: $scope.searchText,
			headers: {'Content-Type': 'application/json'}
		})
		.then(function successCallback(response) {
			$rootScope.search = true;
			//console.log(response.data)
			parseService.parseResults(response.data,1);
		}, function errorCallback(response) {
			console.log(response)
		});
	};

	$scope.deployBlueprint = function (blueprint) {
		//method to deploy blueprint
        /*$http({
            method: 'POST',
            url: "...",
            data: ...,
            headers: {'Content-Type': 'application/json'}
        })
            .then(function successCallback(response) {

            }, function errorCallback(response) {
                console.log(response)
            });*/
		console.log("deployed!!!!!!");
		console.log(blueprint);
    };

});
