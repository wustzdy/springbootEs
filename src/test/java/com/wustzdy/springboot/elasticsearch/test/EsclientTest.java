package com.wustzdy.springboot.elasticsearch.test;

import com.wustzdy.springboot.elasticsearch.SpringBootElasticsearchApplication;
import com.wustzdy.springboot.elasticsearch.bean.EsClient;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootElasticsearchApplication.class)
public class EsclientTest {

    @Autowired
    private EsClient esClient;
    String index="person";
    String type="main";

    @Test
    public void createIndex() throws IOException {
        Settings.Builder settings = Settings.builder().put("number_of_replicas", 1).put("number_of_shards", 5);
        //关于准备索引的结构的aappings
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("name")
                           .field("type","text")
                        .endObject()
                        .startObject("age")
                            .field("type","integer")
                        .endObject()
                        .startObject("birthday")
                             .field("type","date")
                             .field("format","yyyy-MM-dd")
                        .endObject()
                    .endObject()
                .endObject();

        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type,mappings);
        RestHighLevelClient client = esClient.restHighLevelClient();
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("创建成功，创建的索引名为：" + response.toString());
    }
}
