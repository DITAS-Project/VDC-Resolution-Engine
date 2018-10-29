package com.ditas.resolutionengine.Controllers;


import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.ditas.resolutionengine.Entities.Requirements;
import com.ditas.resolutionengine.Services.DURERequestService;
import com.ditas.resolutionengine.Services.EsSearchService;
import com.ditas.resolutionengine.Services.ResolutionEngineService;



@RestController
public class BlueprintController {
	
	@Autowired
	EsSearchService searchService;
	
	@Autowired
	ResolutionEngineService resolutionService;
	
	@Autowired
	DURERequestService dureService;
	
	@RequestMapping(method=RequestMethod.POST , value="/searchBlueprint")
	public String searchBP(@RequestBody String searchText){
		
	
		System.out.println("Search text: "+searchText);
		String response = searchService.blueprintSearch(searchText);
		
		return response;
	}
	

	@RequestMapping(method=RequestMethod.POST , value="/searchBlueprintByReq")
	public String searchBPByRequirements(@RequestBody String  applicationRequirements){
		
		Requirements requirements = new Requirements();
		
		JSONObject applicationRequirements_json = new JSONObject(applicationRequirements);
		
		org.json.JSONArray method_tags = applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("methodTags");
		requirements.setMethodTags(method_tags);
		
		org.json.JSONArray vdc_tags =  applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("vdcTags");
		requirements.setVdcTags(vdc_tags);
	
		String response = resolutionService.ResolutionEngineRequest(requirements, applicationRequirements_json);
		
		return response;
	
	
	}
	
	@RequestMapping(method=RequestMethod.POST , value="/searchBlueprintByReq_ESresponse")
	public String searchBPByRequirements_esResponse(@RequestBody String  applicationRequirements){
		
		Requirements requirements = new Requirements();
		
		JSONObject applicationRequirements_json = new JSONObject(applicationRequirements);
		
		org.json.JSONArray method_tags = applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("methodTags");
		requirements.setMethodTags(method_tags);
		
		org.json.JSONArray vdc_tags =  applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("vdcTags");
		requirements.setVdcTags(vdc_tags);
	
		String esResponse = searchService.blueprintSearchByReq(requirements);
		
		
		return esResponse;
	
	
	}
	
	@RequestMapping(method=RequestMethod.POST , value="/searchBlueprintByReq_DureRequest")
	public String searchBPByRequirements_DureRequest(@RequestBody String  applicationRequirements){
		
		Requirements requirements = new Requirements();
		
		JSONObject applicationRequirements_json = new JSONObject(applicationRequirements);
		
		org.json.JSONArray method_tags = applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("methodTags");
		requirements.setMethodTags(method_tags);
		
		org.json.JSONArray vdc_tags =  applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("vdcTags");
		requirements.setVdcTags(vdc_tags);
	
		String esResponse = searchService.blueprintSearchByReq(requirements);
		String dureRequest = dureService.createRequest(esResponse, applicationRequirements_json);
		
		return dureRequest;
	
	
	}
	
	
	@RequestMapping(method=RequestMethod.GET , value="/GetIntermediateBlueprint")
	public String GetIntermediateBlueprint(){
		
		String requirements_file = "";
		Resource resource = new ClassPathResource("user_reqs.json");
		try {
			InputStream resourceInputStream = resource.getInputStream();
			int data = resourceInputStream.read();
			while(data != -1) {
				  requirements_file += (char) data;
				  data =  resourceInputStream.read();
				}
			
			
			Requirements requirements = new Requirements();
			
			JSONObject applicationRequirements_json = new JSONObject(requirements_file);
			
			org.json.JSONArray method_tags = applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("methodTags");
			requirements.setMethodTags(method_tags);
			
			org.json.JSONArray vdc_tags =  applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("vdcTags");
			requirements.setVdcTags(vdc_tags);
		
			String response = resolutionService.ResolutionEngineRequest(requirements, applicationRequirements_json);
			
			JSONArray response_json = new JSONArray(response);
			return response_json.getJSONObject(0).getJSONObject("blueprint").toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		/*
		Requirements requirements = new Requirements();
		
		JSONObject applicationRequirements_json = new JSONObject(applicationRequirements);
		
		org.json.JSONArray method_tags = applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("methodTags");
		requirements.setMethodTags(method_tags);
		
		org.json.JSONArray vdc_tags =  applicationRequirements_json.getJSONObject("functionalRequirements").getJSONArray("vdcTags");
		requirements.setVdcTags(vdc_tags);
	
		String esResponse = searchService.blueprintSearchByReq(requirements);
		String dureRequest = dureService.createRequest(esResponse, applicationRequirements_json);
		
		return dureRequest;
	*/
	
	}
}
