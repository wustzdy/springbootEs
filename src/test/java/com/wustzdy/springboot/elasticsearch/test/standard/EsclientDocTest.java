package com.wustzdy.springboot.elasticsearch.test.standard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wustzdy.springboot.elasticsearch.SpringBootElasticsearchApplication;
import com.wustzdy.springboot.elasticsearch.bean.EsClient;
import com.wustzdy.springboot.elasticsearch.bean.entity.Person;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
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
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootElasticsearchApplication.class)
public class EsclientDocTest {

    @Autowired
    private EsClient esClient;
    String index = "person";
    String type = "man";

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

    @Test
    public void update() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "武汉科技大学");
        String docId = "1";

        UpdateRequest updateRequest = new UpdateRequest(index, type, docId);
        updateRequest.doc(map);
        RestHighLevelClient client = esClient.restHighLevelClient();
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println("更新文档：" + updateResponse.getResult().toString());

    }

    @Test
    public void deleteDoc() throws IOException {
        DeleteRequest deleteIndexRequest = new DeleteRequest(index, type, "1");
        RestHighLevelClient client = esClient.restHighLevelClient();
        DeleteResponse delete = client.delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println("删除文档：" + delete.getResult().toString());
    }


}
