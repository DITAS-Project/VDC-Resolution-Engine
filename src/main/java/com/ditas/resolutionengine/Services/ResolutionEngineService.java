package com.ditas.resolutionengine.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditas.resolutionengine.Entities.Requirements;

@Service
public class ResolutionEngineService {
	
	@Autowired
	EsSearchService esService;
	
	@Autowired
	DURERequestService dureService;
	
	public String ResolutionEngineRequest(Requirements requirements) {
		
		String esResponse = esService.blueprintSearchByReq(requirements);
		String dureRequest = dureService.createRequest(esResponse);
				
		return dureRequest;
	}

}
