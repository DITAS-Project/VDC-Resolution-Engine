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

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RepositoryRequestService {
	
    @Value("${repository.blueprints}")
    private String repositoryBlueprintsPath;
	
	public ArrayList<JSONObject> fetchFromRepository(ArrayList<String> idsList) {
		
		ArrayList<JSONObject> blueprints = new ArrayList<JSONObject>();
		
		try {
				String repository_request = buildRepositoryRequest(idsList);
				URL url = new URL("http://"+repositoryBlueprintsPath+"?filter="+repository_request);
				Scanner scanner = new Scanner(url.openStream());
				String response = scanner.useDelimiter("\\Z").next();
				scanner.close();
				//System.out.println(response_blueprint);
				
				JSONObject response_json = new JSONObject(response);
				JSONArray blueprints_array = response_json.getJSONArray("_embedded");
				
				for (int i = 0 ; i < blueprints_array.length() ; i++) {
					JSONObject blueprint = blueprints_array.getJSONObject(i);
					blueprints.add(blueprint);
				}
				
				
				return blueprints;

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String buildRepositoryRequest(ArrayList<String> idsList) {
		JSONObject repository_request_json = new JSONObject();
		JSONObject repository_ids_json = new JSONObject();
		
		JSONArray ids_array = new JSONArray();
		for (String id : idsList) {
			JSONObject id_json = new JSONObject();
			id_json.put("$oid", id);
			ids_array.put(id_json);
		}
		repository_ids_json.put("$in", ids_array);
		repository_request_json.put("_id",repository_ids_json);
		
		return repository_request_json.toString();
	}
}
