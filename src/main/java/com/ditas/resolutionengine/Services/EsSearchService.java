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

import javafx.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.support.QueryInnerHitBuilder;
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

			HttpClient httpClient;
			if(EsAuth.equals("basic")) {
				CredentialsProvider provider = new BasicCredentialsProvider();
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(EsUser, EsPass);
				provider.setCredentials(AuthScope.ANY, credentials);
				httpClient= HttpClientBuilder.create()
						.setDefaultCredentialsProvider(provider)
						.build();
			}else{
				httpClient=HttpClientBuilder.create()
						.build();
			}

			try {
				HttpPost request = new HttpPost("http://" + EsHost + ":" + EsPort + "/" + EsIndex + "/_search");
				request.addHeader("content-type", "application/json");
				StringEntity params =new StringEntity(query);
				request.setEntity(params);
				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				String responseString = new BasicResponseHandler().handleResponse(response);
				if (statusCode != 200) {
					System.err.println(responseString);
				}
				return responseString;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public String blueprintSearchByReq(Requirements requirements) {
		System.out.println("Basic " + (new String(Base64.encodeBase64((EsUser+":"+EsPass).getBytes()))));
		try {
			String query2 = "{\"query\":\n" +
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
			System.out.println(query2);
			String query="{\n" +
					"  \"query\":{\n" +
					"     \"bool\":{\n" +
					"        \"should\":[\n" +
					"           {\n" +
					"              \"function_score\":{\n" +
					"                 \"query\":{\n" +
					"                    \"bool\":{\n" +
					"                       \"must\":{\n" +
					"                          \"match_all\":{\n" +
					"                          }\n" +
					"                       },\n" +
					"                       \"filter\":[\n" +
					"                          {\n" +
					"                             \"match\":{\n" +
					"                                \"description\":{\n" +
					"                                   \"query\" : \""+ requirements.getVdcTags()+" \"\n" +
					"                                }\n" +
					"                             }\n" +
					"                          }\n" +
					"                       ]\n" +
					"                    }\n" +
					"                 },\n" +
					"                 \"field_value_factor\":{\n" +
					"                    \"field\":\"factor\"\n" +
					"                 },\n" +
					"                 \"boost\":4\n" +
					"              }\n" +
					"           },\n" +
					"           {\n" +
					"              \"function_score\":{\n" +
					"                 \"query\":{\n" +
					"                    \"bool\":{\n" +
					"                       \"must\":{\n" +
					"                          \"match_all\":{\n" +
					"                          }\n" +
					"                       },\n" +
					"                       \"filter\":[\n" +
					"                          {\n" +
					"                             \"nested\":{\n" +
					"                                \"query\":{\n" +
					"                                   \"match\":{\n" +
					"                                      \"tags.tags\":{\n" +
					"                                         \"query\" : \""+requirements.getMethodTags()+" \",\n" +
					"                                      }\n" +
					"                                   }\n" +
					"                                },\n" +
					"                                \"path\":\"tags\",\n" +
					"                                \"inner_hits\":{\n" +
					"                                }\n" +
					"                             }\n" +
					"                          }\n" +
					"                       ]\n" +
					"                    }\n" +
					"                 },\n" +
					"                 \"field_value_factor\":{\n" +
					"                    \"field\":\"factor\"\n" +
					"                 },\n" +
					"                 \"boost\":8\n" +
					"              }\n" +
					"           }\n" +
					"        ]\n" +
					"     }\n" +
					"  }\n" +
					"}";
			HttpClient httpClient;
			if(EsAuth.equals("basic")) {
				CredentialsProvider provider = new BasicCredentialsProvider();
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(EsUser, EsPass);
				provider.setCredentials(AuthScope.ANY, credentials);
				httpClient= HttpClientBuilder.create()
						.setDefaultCredentialsProvider(provider)
						.build();
			}else{
				httpClient=HttpClientBuilder.create()
						.build();
			}

			try {
				HttpPost request = new HttpPost("http://" + EsHost + ":" + EsPort + "/" + EsIndex + "/_search?scroll=3m&size=50");
				request.addHeader("content-type", "application/json");
				StringEntity params =new StringEntity(query);
				request.setEntity(params);
				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				String responseString = new BasicResponseHandler().handleResponse(response);
				if (statusCode != 200) {
					System.err.println(responseString);
				}
				return responseString;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	


}
