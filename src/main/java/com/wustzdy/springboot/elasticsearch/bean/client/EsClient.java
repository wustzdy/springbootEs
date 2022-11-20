package com.wustzdy.springboot.elasticsearch.bean.client;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsClient {
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        //若有多个，可以传一个数组
                        new HttpHost("192.168.1.5", 9200, "http")));
        System.out.println("connect success");
        return client;
    }
}
