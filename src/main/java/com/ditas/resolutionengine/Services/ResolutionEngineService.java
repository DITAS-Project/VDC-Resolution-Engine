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
