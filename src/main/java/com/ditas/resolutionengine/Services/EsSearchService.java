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


    public String blueprintSearchByReq(Requirements requirements) { 
                         
        SemanticManagerService manager = new SemanticManagerService();
        
        String query = "";
        
        try {
			
            query = manager.createSemanticElasticSearchQuery(requirements);
                        
            System.out.println(query);
                        
            HttpClient httpClient;
            if(EsAuth.equals("basic")) {
                        
                CredentialsProvider provider = new BasicCredentialsProvider();
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(EsUser, EsPass);
                provider.setCredentials(AuthScope.ANY, credentials);
                httpClient= HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider)
                .build();
			
            }
            else{
				
                httpClient=HttpClientBuilder.create()
                .build();
			
            }

            try {
				
                HttpPost request = new HttpPost("http://" + EsHost + ":" + EsPort + "/" + EsIndex + "/_search?scroll=3m&size=20");
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
				
                e.printStackTrace();
			
            }
		
        } catch (Exception e) {
			
            e.printStackTrace();
		
        }
		
        return null;
	
    }
}