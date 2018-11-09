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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.support.QueryInnerHitBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ditas.resolutionengine.Configurations.ElasticSearchConfig;
import com.ditas.resolutionengine.Entities.Requirements;

@Service
public class EsSearchService {
	
	@Autowired
	ElasticSearchConfig config;
	
	@Value("${elasticsearch.index}")
    private String EsIndex;
	
	@Value("${elasticsearch.type}")
    private String EsType;
	
	public String blueprintSearch(String searchText) {
		try {
			Client client = config.client();
			
			QueryBuilder qb = boolQuery()
					.should(matchQuery("description", searchText))
					.should(nestedQuery("tags", matchQuery("tags.tags", searchText).boost(8))
							.innerHit(new QueryInnerHitBuilder().setFetchSource("method_id", null))
					);
			
			SearchResponse response = client.prepareSearch(EsIndex)
					.setTypes(EsType)
					.setQuery(qb)
					.execute().actionGet();
			
			
			return response.toString();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public String blueprintSearchByReq(Requirements requirements) {
		try {
			Client client = config.client();
			
			QueryBuilder qb = boolQuery()
					.should(matchQuery("description", requirements.getVdcTags()))
					.should(nestedQuery("tags", matchQuery("tags.tags", requirements.getMethodTags()).boost(8))
							.innerHit(new QueryInnerHitBuilder().setFetchSource("method_id", null))
					);
			
			SearchResponse response = client.prepareSearch(EsIndex)
					.setTypes(EsType)
					.setQuery(qb)
					.execute().actionGet();
			
			
			return response.toString();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	


}
