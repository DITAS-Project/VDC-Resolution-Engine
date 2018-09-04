package com.ditas.resolutionengine.Services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditas.resolutionengine.Entities.Requirements;

@Service
public class ResolutionEngineService {
	
	@Autowired
	EsSearchService esService;
	
	@Autowired
	DURERequestService dureService;
	
	public String ResolutionEngineRequest(Requirements requirements, JSONObject app_requirements) {
		
		String esResponse = esService.blueprintSearchByReq(requirements);
		String dureRequest = dureService.createRequest(esResponse, app_requirements);
		String dureResponse = dureService.sendRequest(dureRequest);
				
		return dureResponse;
	}

}
