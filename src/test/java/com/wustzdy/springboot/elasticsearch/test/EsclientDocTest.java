package com.wustzdy.springboot.elasticsearch.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wustzdy.springboot.elasticsearch.SpringBootElasticsearchApplication;
import com.wustzdy.springboot.elasticsearch.bean.EsClient;
import com.wustzdy.springboot.elasticsearch.bean.Person;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootElasticsearchApplication.class)
public class EsclientDocTest {

    @Autowired
    private EsClient esClient;
    String index = "person";
    String type = "main";

    ObjectMapper objectMapper = new ObjectMapper();

    //#添加文档，手动指定id
    @Test
    public void createDoc() throws IOException {

        Person person = new Person(1, "张三", 23, new Date());
        String json = objectMapper.writeValueAsString(person);

        IndexRequest request = new IndexRequest(index, type, person.getId().toString());
        request.source(json, XContentType.JSON);
        RestHighLevelClient client = esClient.restHighLevelClient();
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("添加文档：" + response.getResult().toString());

    }
}