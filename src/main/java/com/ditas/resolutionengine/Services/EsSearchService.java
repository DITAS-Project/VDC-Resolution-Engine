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
							.innerHit(new QueryInnerHitBuilder().setFetchSource("method_name", null))
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
