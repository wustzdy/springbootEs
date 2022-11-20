package com.wustzdy.springboot.elasticsearch.test.standard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wustzdy.springboot.elasticsearch.SpringBootElasticsearchApplication;
import com.wustzdy.springboot.elasticsearch.bean.client.EsClient;
import com.wustzdy.springboot.elasticsearch.bean.entity.Person;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
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
public class EsclientBatchDocTest {
    @Autowired
    private EsClient esClient;
    String index = "person";
    String type = "man";

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createBatchDoc() throws IOException {

        Person person1 = new Person(1, "张三", 23, new Date());
        Person person2 = new Person(2, "李四", 24, new Date());
        Person person3 = new Person(3, "王武", 25, new Date());

        String json1 = objectMapper.writeValueAsString(person1);
        String json2 = objectMapper.writeValueAsString(person2);
        String json3 = objectMapper.writeValueAsString(person3);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(index, type, person1.getId().toString()).source(json1, XContentType.JSON));
        bulkRequest.add(new IndexRequest(index, type, person2.getId().toString()).source(json2, XContentType.JSON));
        bulkRequest.add(new IndexRequest(index, type, person3.getId().toString()).source(json3, XContentType.JSON));

        RestHighLevelClient client = esClient.restHighLevelClient();
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("bulk添加文档：" + response.toString());

    }

    @Test
    public void bulkDocDelete() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new DeleteRequest(index, type, "1"));
        bulkRequest.add(new DeleteRequest(index, type, "2"));
        bulkRequest.add(new DeleteRequest(index, type, "3"));

        RestHighLevelClient client = esClient.restHighLevelClient();
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("bulk删除文档：" + response.toString());

    }
}
