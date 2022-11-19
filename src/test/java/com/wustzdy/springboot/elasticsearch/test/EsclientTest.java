package com.wustzdy.springboot.elasticsearch.test;

import com.wustzdy.springboot.elasticsearch.bean.EsClient;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class EsclientTest {

    @Autowired
    private EsClient esClient;

    @Test
    public void createIndex() throws IOException {
        Settings.Builder settings = Settings.builder().put("number_of_replicas", 1).put("number_of_shards", 5);
        //关于准备索引的结构的aappings
        JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("name")
                           .field("type","text")
                        .endObject()
                        .startObject("age")
                            .field("type","integer")
                        .endObject()
                        .startObject("age")
                             .field("type","date")
                             .field("format","yyyy-MM-dd")
                        .endObject()
                    .endObject()
                .endObject();

        CreateIndexRequest request = new CreateIndexRequest("xk_index");//索引名
        CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("创建成功，创建的索引名为：" + response.index());
    }
}
