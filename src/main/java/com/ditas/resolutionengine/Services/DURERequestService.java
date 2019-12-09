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

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DURERequestService {
	
    @Autowired
    private RepositoryRequestService repositoryService;

    @Value("${rphost}")
    private String rphost;

    @Value("${dure.blueprints.path}")
    private String dureBlueprintsPath;
    
    @Value("${dure.blueprints.port}")
    private String dureBlueprintsPort;

    public String createRequest(String elasticResponse, JSONObject app_requirements) {
		
        //Get the JSON response from Elastic Search for further parsing
        JSONObject elastic_response_json = new JSONObject(elasticResponse);
		
        //Parse the number of returned blueprints from Elastic Search response
        int num_of_hits = elastic_response_json.getJSONObject("hits").getJSONArray("hits").length();
		
        //Here will be stored all the ids of the returned blueprints in order to retrieve them from the Blueprint Repository
        ArrayList<String> idsList = new ArrayList<String>();
		
        //Here will be stored all the method names that had their tags matched (during the Elastic Search search) for its blueprint
        HashMap<String, ArrayList<String>> methodsList = new HashMap<String, ArrayList<String>>();
	
        JSONObject esResult;	
        String bp_id;
        for (int bp_index = 0 ; bp_index < num_of_hits ; bp_index++) {
			
            esResult = elastic_response_json.getJSONObject("hits").getJSONArray("hits").getJSONObject(bp_index);
            bp_id = esResult.get("_id").toString();
            idsList.add(bp_id);
            ArrayList<String> list_of_methods = new ArrayList<String>();
                        
            JSONArray result_innerHits_exact_synonym =  esResult.getJSONObject("inner_hits").getJSONObject("exact_synonym").getJSONObject("hits").getJSONArray("hits");
			
            //Parse the number of matched methods of the current blueprint from Elastic Search response
            int num_of_matched_methods_exact_synonym = result_innerHits_exact_synonym.length();
			
            for (int method_index_exact_synonym = 0 ; method_index_exact_synonym < num_of_matched_methods_exact_synonym ; method_index_exact_synonym++) {
				
                String method =  result_innerHits_exact_synonym.getJSONObject(method_index_exact_synonym).getJSONObject("_source").get("method_id").toString();
                list_of_methods.add(method);		
			
            }
                        
            JSONArray result_innerHits_parent_child =  esResult.getJSONObject("inner_hits").getJSONObject("parent_child").getJSONObject("hits").getJSONArray("hits");
			
            //Parse the number of matched methods of the current blueprint from Elastic Search response
            int num_of_matched_methods_parent_child = result_innerHits_parent_child.length();
			
            for (int method_index_parent_child = 0 ; method_index_parent_child < num_of_matched_methods_parent_child ; method_index_parent_child++) {
				
                String method =  result_innerHits_parent_child.getJSONObject(method_index_parent_child).getJSONObject("_source").get("method_id").toString();
                if(!list_of_methods.contains(method)) {
                                    
                    list_of_methods.add(method);
                                
                }
            }
                        
            JSONArray result_innerHits_sibling =  esResult.getJSONObject("inner_hits").getJSONObject("sibling").getJSONObject("hits").getJSONArray("hits");
			
            //Parse the number of matched methods of the current blueprint from Elastic Search response
            int num_of_matched_methods_sibling = result_innerHits_sibling.length();
			
            for (int method_index_sibling = 0 ; method_index_sibling < num_of_matched_methods_sibling ; method_index_sibling++) {
				
                String method =  result_innerHits_sibling.getJSONObject(method_index_sibling).getJSONObject("_source").get("method_id").toString();
                if(!list_of_methods.contains(method)) {
                                    
                    list_of_methods.add(method);
                                
                }
            }
            
            methodsList.put(bp_id, list_of_methods);		
			
        }
		
        //Fetch the blueprints from repository and store them in a list
        ArrayList<JSONObject> blueprints = repositoryService.fetchFromRepository(idsList);
        String dure_request = buildDURERequest(blueprints, methodsList, app_requirements);
		
        return dure_request;
		
    }
	
	
    public String buildDURERequest(ArrayList<JSONObject> blueprints, HashMap<String, ArrayList<String>> methodsList, JSONObject app_requirements) {
		
        JSONObject dure_request_json = new JSONObject();
		
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
		
        HttpClient httpClient= HttpClientBuilder.create().build();

        try {
            
            HttpPost request = new HttpPost("http://"+rphost+":"+dureBlueprintsPort+dureBlueprintsPath);
            request.addHeader("content-type", "application/json");
            StringEntity params =new StringEntity(dureRequest);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String responseString = new BasicResponseHandler().handleResponse(response);
			
            return responseString;
			
        } catch (Exception e) {
		
            e.printStackTrace();
		
        }
		
        return null;
    }
}