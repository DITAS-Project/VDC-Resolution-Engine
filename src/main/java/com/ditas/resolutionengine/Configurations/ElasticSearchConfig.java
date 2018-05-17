package com.ditas.resolutionengine.Configurations;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
	@Value("${elasticsearch.host}")
    private String EsHost;

    @Value("${elasticsearch.port}")
    private int EsPort;

    @Value("${elasticsearch.clustername}")
    private String EsClusterName;


   @Bean
    public Client client() throws Exception {

        Settings esSettings = Settings.settingsBuilder()
                .put("cluster.name", EsClusterName)
				.put("shield.user", "es_admin:esadmin")
                .build();
        return TransportClient.builder()
				.addPlugin(ShieldPlugin.class)
                .settings(esSettings)
                .build()
                .addTransportAddress(
				  new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
   
    }
}
