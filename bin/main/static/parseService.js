angular.module('app')
.service('parseService', function ($rootScope) {
	
	this.parseResults = function (searchResults){
		console.log(searchResults)
		
		var results = new Array();
		
		
		for (var i = 0 ; i < searchResults.length ; i++){
			var blueprint_overview = searchResults[i].blueprint.INTERNAL_STRUCTURE.Overview
			var id = searchResults[i].blueprint._id;
			var score = searchResults[i].score;
			var methods_list = searchResults[i].methodNames;
			var methods = new Array();
			for (var j = 0; j < methods_list.length ; j++){
				methods.push(methods_list[j])
			}
			var result = {
					"id":id,
					"name": blueprint_overview.name,
					"description": blueprint_overview.description,
					"score": score,
					"methods": methods
			}
			results.push(result)
		}
		
		console.log(results)
		$rootScope.results = results
			
	}
})
