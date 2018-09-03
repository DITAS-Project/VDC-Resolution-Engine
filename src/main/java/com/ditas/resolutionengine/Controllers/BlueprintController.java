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
import com.ditas.resolutionengine.Services.ResolutionEngineService;


@RestController
public class BlueprintController {
	
	@Autowired
	EsSearchService searchService;
	
	@Autowired
	ResolutionEngineService resolutionService;
	
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
		
		ArrayList<String> method_tags = (ArrayList<String>) ((Map<String, Object>) searchText.get("functionalRequirements")).get("methodTags");
		requirements.setMethodTags(method_tags);
		
		ArrayList<String> vdc_tags = (ArrayList<String>) ((Map<String, Object>) searchText.get("functionalRequirements")).get("vdcTags");
		requirements.setVdcTags(vdc_tags);
		
		
		//System.out.println(requirements.getMethodTags());
		//System.out.println(requirements.getVdcTags());
		
		String response = resolutionService.ResolutionEngineRequest(requirements);
		
		return response;
	
	}
}
