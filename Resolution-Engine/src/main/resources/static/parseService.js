angular.module('app')
.service('parseService', function ($rootScope) {
	
	this.parseResults = function (searchResults){
		console.log(searchResults)
		
		var results = new Array();
		
		var hits = searchResults.hits.hits;
		
		for (var i = 0 ; i < hits.length; i++){
			var inner_hits = hits[i].inner_hits.tags.hits.hits
			var methods = new Array();
			for (var j = 0; j < inner_hits.length ; j++){
				methods.push(inner_hits[j]._source.method_name)
			}
			var result = {
					"id": hits[i]._id,
					"description": hits[i]._source.description,
					"methods": methods
			}
			results.push(result)
		}
		
		console.log(results)
		$rootScope.results = results
			
	}
})