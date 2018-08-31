package com.ditas.resolutionengine.Controllers;

import java.util.ArrayList;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ditas.resolutionengine.Entities.Requirements;
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST , value="/searchBlueprintByReq")
	public String searchBPByRequirements(@RequestBody Map<String, Object> searchText){
		
		Requirements requirements = new Requirements();
		
		ArrayList<String> method_tags = (ArrayList<String>) ((Map<String, Object>) searchText.get("FunctionalRequirements")).get("methodTags");
	//	System.out.println(method_tags.get(0));
		requirements.setMethodTags(method_tags);
		
		ArrayList<String> vdc_tags = (ArrayList<String>) ((Map<String, Object>) searchText.get("FunctionalRequirements")).get("vdcTags");
	//	System.out.println(vdc_tags.get(1));
		requirements.setVdcTags(vdc_tags);
		
		
		System.out.println(requirements.getMethodTags());
		System.out.println(requirements.getVdcTags());
		
		String response = searchService.blueprintSearchByReq(requirements);
		
		return response;
	
	}
}
