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


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DURERequestService {
	
	@Autowired
	private RepositoryRequestService repositoryService;
	
	@Value("${host")
    private String host;
    
    @Value("${dure.blueprints.path}")
    private String dureBlueprintsPath;
    
    @Value("${dure.blueprints.port}")
    private String dureBlueprintsPort;

	public String createRequest(String elasticResponse, JSONObject app_requirements) {
		
		//Get the JSON response from Elastic Search for further parsing
		JSONObject elastic_response_json = new JSONObject(elasticResponse);
		
		
		//Parse the number of returned blueprints from Elastic Search response
		int num_of_hits = Integer.parseInt(elastic_response_json.getJSONObject("hits").get("total").toString());
		
		//Here will be stored all the ids of the returned blueprints in order to retrieve them from the Blueprint Repository
		ArrayList<String> idsList = new ArrayList<String>();
		
		//Here will be stored all the method names that had their tags matched (during the Elastic Search search) for its blueprint
		HashMap<String, ArrayList<String>> methodsList = new HashMap<String, ArrayList<String>>();
		//System.out.println("eksw");
		//System.out.println(elastic_response_json);
		JSONObject esResult;	
		String bp_id;
		for (int bp_index = 0 ; bp_index < num_of_hits ; bp_index++) {
			//System.out.println("mesa1");
			esResult = elastic_response_json.getJSONObject("hits").getJSONArray("hits").getJSONObject(bp_index);
			bp_id = esResult.get("_id").toString();
			idsList.add(bp_id);
			
			JSONObject result_innerHits =  esResult.getJSONObject("inner_hits").getJSONObject("tags").getJSONObject("hits");
			
			//Parse the number of matched methods of the current blueprint from Elastic Search response
			//int num_of_matched_methods = Integer.parseInt(result_innerHits.get("total").toString());
			int num_of_matched_methods = result_innerHits.getJSONArray("hits").length();
			//System.out.println("mesa2");
			ArrayList<String> list_of_methods = new ArrayList<String>();
			for (int method_index = 0 ; method_index < num_of_matched_methods ; method_index++) {
				//System.out.println("mesa3");
				String method =  result_innerHits.getJSONArray("hits").getJSONObject(method_index).getJSONObject("_source").get("method_id").toString();
				list_of_methods.add(method);		
			}
			methodsList.put(bp_id, list_of_methods);		
			
		}
		//System.out.println("telos");
		
	
		
		//Fetch the blueprints from repository and store them in a list
		ArrayList<JSONObject> blueprints = repositoryService.fetchFromRepository(idsList);		
		System.out.println(methodsList.toString());
		String dure_request = buildDURERequest(blueprints, methodsList, app_requirements);
		System.out.println(dure_request);

		
		return dure_request;
		
	}
	
	public String buildDURERequest(ArrayList<JSONObject> blueprints, HashMap<String, ArrayList<String>> methodsList, JSONObject app_requirements) {
		JSONObject dure_request_json = new JSONObject();
		
		//JSONObject requirements = new JSONObject(app_requirements);
		
		ArrayList<Object> list = new ArrayList<Object>();
		JSONObject current_blueprint ;
		String current_id;
		for (int i = 0; i < blueprints.size() ; i++) {
			JSONObject json = new JSONObject();
			current_blueprint = blueprints.get(i);
			current_id = (String) current_blueprint.get("_id");
			json.put("blueprint", current_blueprint);
			json.put("methodNames", methodsList.get(current_id));
			list.add(json);
		}
		dure_request_json.put("applicationRequirements", app_requirements);
		dure_request_json.put("candidates", list);
		
		
		return dure_request_json.toString();
	}
	
	
	

	public String sendRequest(String dureRequest) {
		// TODO Auto-generated method stub
		
		URL url;
		
		try {
			
			url = new URL("http://"+host+":"+dureBlueprintsPort+dureBlueprintsPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			OutputStream os = conn.getOutputStream();
			os.write(dureRequest.getBytes());
			os.flush();
			

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			String response = "";
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				response += output+"\n";
			}

			conn.disconnect();
			
			return response;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return null;
	}
}
