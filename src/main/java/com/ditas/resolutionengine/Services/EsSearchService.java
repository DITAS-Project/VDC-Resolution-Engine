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

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import javafx.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.support.QueryInnerHitBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ditas.resolutionengine.Entities.Requirements;

@Service
public class EsSearchService {
	
	@Value("${elasticsearch.index}")
    private String EsIndex;

	@Value("${eshost}")
	private String EsHost;

    @Value("${elasticsearch.auth}")
    private String EsAuth;

    @Value("${elasticsearch.user}")
    private String EsUser;

    @Value("${elasticsearch.pass}")
    private String EsPass;

	@Value("${elasticsearch.port}")
	private int EsPort;
	
	public String blueprintSearch(String searchText) {
		try {
			String query = "{\"query\":\n" +
					"{\n" +
					"  \"bool\" : {\n" +
					"    \"should\" : [ {\n" +
					"      \"match\" : {\n" +
					"        \"description\" : {\n" +
					"          \"query\" : \""+ searchText+" \"\n" +
					"        }\n" +
					"      }\n" +
					"    }, {\n" +
					"      \"nested\" : {\n" +
					"        \"query\" : {\n" +
					"          \"match\" : {\n" +
					"            \"tags.tags\" : {\n" +
					"              \"query\" : \""+searchText+" \",\n" +
					"              \"boost\" : 8.0\n" +
					"            }\n" +
					"          }\n" +
					"        },\n" +
					"        \"path\" : \"tags\",\n" +
					"        \"inner_hits\" : {}\n" +
					"        }\n" +
					"      }\n" +
					"    } ]\n" +
					"  }\n" +
					"}\n" +
					"}";
			HttpResponse<String> response;
			if(EsAuth.equals("basic")){
                response = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + EsIndex + "/_search")
                        .header("Authorization", "Basic " + (new String(Base64.encodeBase64((EsUser+":"+EsPass).getBytes()))))
                        .header("Content-Type", "application/json")
                        .header("cache-control", "no-cache")
                        .header("Method", "POST")
                        .body(query)
                        .asString();
            }else {
                response = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + EsIndex + "/_search")
                        .header("Content-Type", "application/json")
                        .header("cache-control", "no-cache")
                        .header("Method", "POST")
                        .body(query)
                        .asString();
            }

			if (response.getCode() != 200) {
				System.err.println(response.getBody());
			}


			return response.getBody();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public String blueprintSearchByReq(Requirements requirements) {
		System.out.println("Basic " + (new String(Base64.encodeBase64((EsUser+":"+EsPass).getBytes()))));
		try {
			String query = "{\"query\":\n" +
					"{\n" +
					"  \"bool\" : {\n" +
					"    \"should\" : [ {\n" +
					"      \"match\" : {\n" +
					"        \"description\" : {\n" +
					"          \"query\" : \""+ requirements.getVdcTags()+" \"\n" +
					"        }\n" +
					"      }\n" +
					"    }, {\n" +
					"      \"nested\" : {\n" +
					"        \"query\" : {\n" +
					"          \"match\" : {\n" +
					"            \"tags.tags\" : {\n" +
					"              \"query\" : \""+requirements.getMethodTags()+" \",\n" +
					"              \"boost\" : 8.0\n" +
					"            }\n" +
					"          }\n" +
					"        },\n" +
					"        \"path\" : \"tags\",\n" +
					"        \"inner_hits\" : {\n" +
//					"          \"_source\" : {\n" +
//					"            \"includes\" : [ \"method_id\" ],\n" +
//					"            \"excludes\" : [ ]\n" +
//					"          }\n" +
					"        }\n" +
					"      }\n" +
					"    } ]\n" +
					"  }\n" +
					"}\n" +
					"}";
			HttpResponse<String> response;
            if(EsAuth.equals("basic")) {
                response = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + EsIndex + "/_search")
						.header("Authorization", "Basic " + (new String(Base64.encodeBase64((EsUser+":"+EsPass).getBytes()))))
                        .header("Content-Type", "application/json")
                        .header("cache-control", "no-cache")
                        .header("Method", "POST")
                        .body(query)
                        .asString();
            }else {
                response = Unirest.post("http://" + EsHost + ":" + EsPort + "/" + EsIndex + "/_search")
                        .header("Content-Type", "application/json")
                        .header("cache-control", "no-cache")
                        .header("Method", "POST")
                        .body(query)
                        .asString();
            }

			if (response.getCode() != 200) {
				System.err.println(response.getBody());
			}


			return response.getBody();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	


}
