package com.ditas.resolutionengine.Services;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ditas.resolutionengine.Configurations.ElasticSearchConfig;

@Service
public class EsSearchService {
	
	@Autowired
	ElasticSearchConfig config;
	
	public String blueprintSearch(String searchText) {
		try {
			Client client = config.client();
			
			QueryBuilder qb = boolQuery();
			
			SearchResponse response = client.prepareSearch("ditas")
					.setTypes("blueprints")
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
