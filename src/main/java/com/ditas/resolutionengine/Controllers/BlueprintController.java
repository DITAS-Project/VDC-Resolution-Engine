package com.ditas.resolutionengine.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ditas.resolutionengine.Services.EsSearchService;

@RestController
public class BlueprintController {
	
	@Autowired
	EsSearchService searchService;
	
	@RequestMapping(method=RequestMethod.POST , value="/searchBlueprint")
	public String searchBP(@RequestBody String searchText){
		
		System.out.println("Search text: "+searchText);
		String response = searchService.blueprintSearch(searchText);
		
		return response;
	}
}
