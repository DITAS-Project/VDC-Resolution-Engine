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
package com.ditas.resolutionengine.Controllers;

import java.io.IOException;
import java.io.InputStream;

import com.ditas.resolutionengine.Services.PurchaseHandlerService;
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
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
public class BlueprintController {
	
	@Autowired
	EsSearchService searchService;
	
	@Autowired
	ResolutionEngineService resolutionService;
	
	@Autowired
	DURERequestService dureService;

	@Autowired
	PurchaseHandlerService phService;

	@RequestMapping(method=RequestMethod.POST , value="/searchBlueprintByReq")
	@CrossOrigin(origins = "http://localhost:8080")
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

	@RequestMapping(method=RequestMethod.POST , value="/makePurchaces")
	public String makePurchases(@RequestBody String  numberOfPurchases){
		try {
			JSONObject purchNum = new JSONObject(numberOfPurchases);
			int num = purchNum.getInt("num");
			String response = "";
			for(int i =0 ;i<num;i++){
				response+=phService.makePurchase()+"\n";
			}
			response += "\ndone";

			return response;
		}catch(Exception ex){return ex.getStackTrace().toString();}
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
