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
angular.module('app')
.service('parseService', function ($rootScope) {
	
	this.parseResults = function (searchResults) {


			console.log(searchResults)

			var results = new Array();


			for (var i = 0; i < searchResults.length; i++) {
				var blueprint_overview = searchResults[i].blueprint.INTERNAL_STRUCTURE.Overview;
				var id = searchResults[i].blueprint._id;
				var score = searchResults[i].score;
				var userRating = searchResults[i].userRating;
				var methods_list = searchResults[i].methodNames;

				var methods = new Array();
				for (var j = 0; j < methods_list.length; j++) {
					methods.push(methods_list[j])
				}
				if(i==0) {
					var result = {
						"id": id,
						"name": blueprint_overview.name,
						"tags": blueprint_overview.tags,
						"description": blueprint_overview.description,
						"score": score,
						"methods": methods,
						"userRating": userRating,
						"semantic": 63.924446
					}
				}
				if(i==1) {
					var result = {
						"id": id,
						"name": blueprint_overview.name,
						"tags": blueprint_overview.tags,
						"description": blueprint_overview.description,
						"score": score,
						"methods": methods,
						"userRating": userRating,
						"semantic": 16
					}
				}
				results.push(result)
			}

			console.log(results)
			$rootScope.results = results

	}

});
