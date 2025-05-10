package com.blllf.blogease.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class RestHighLevelClientConfig {
    private String host;
    private int port;
    @Bean
    public RestHighLevelClient client(){
        HttpHost http = new HttpHost(host, port, "http");
        return new RestHighLevelClient(RestClient.builder(http));
    }
}
