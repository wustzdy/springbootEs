package com.wustzdy.springboot.elasticsearch.test.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wustzdy.springboot.elasticsearch.SpringBootElasticsearchApplication;
import com.wustzdy.springboot.elasticsearch.bean.JsonUtil.JsonUtil;
import com.wustzdy.springboot.elasticsearch.bean.client.EsClient;
import com.wustzdy.springboot.elasticsearch.bean.entity.SmsLogs;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.BoostingQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootElasticsearchApplication.class)
public class TestData {
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private EsClient esClient;

    String index = "sms-logs-index";
    String type = "sms-logs-type";

    /**
     * 创建索引
     *
     * @throws IOException
     */
    @Test
    public void createSmsLogsIndex() throws IOException {
        //1. settings
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);

        //2. mapping.
        XContentBuilder mapping = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("createDate")
                .field("type", "date")
                .endObject()
                .startObject("sendDate")
                .field("type", "date")
                .endObject()
                .startObject("longCode")
                .field("type", "keyword")
                .endObject()
                .startObject("mobile")
                .field("type", "keyword")
                .endObject()
                .startObject("corpName")
                .field("type", "keyword")
                .endObject()
                .startObject("smsContent")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("state")
                .field("type", "integer")
                .endObject()
                .startObject("operatorId")
                .field("type", "integer")
                .endObject()
                .startObject("province")
                .field("type", "keyword")
                .endObject()
                .startObject("ipAddr")
                .field("type", "ip")
                .endObject()
                .startObject("replyTotal")
                .field("type", "integer")
                .endObject()
                .startObject("fee")
                .field("type", "long")
                .endObject()
                .endObject()
                .endObject();

        //3. 添加索引.
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(settings);
        request.mapping(type, mapping);
        getClient().indices().create(request, RequestOptions.DEFAULT);
        System.out.println("OK!!");
    }

    /**
     * 文档
     *
     * @throws IOException
     */
    @Test
    public void addTestData() throws IOException {
        BulkRequest request = new BulkRequest();

        SmsLogs smsLogs = new SmsLogs();
        smsLogs.setMobile("13800000000");
        smsLogs.setCorpName("途虎养车");
        smsLogs.setCreateDate(new Date());
        smsLogs.setSendDate(new Date());
        smsLogs.setIpAddr("10.126.2.9");
        smsLogs.setLongCode("10690000988");
        smsLogs.setReplyTotal(10);
        smsLogs.setState(0);
        smsLogs.setSmsContent("【途虎养车】亲爱的张三先生/女士，您在途虎购买的货品(单号TH123456)已 到指定安装店多日，" + "现需与您确认订单的安装情况，请点击链接按实际情况选择（此链接有效期为72H）。您也可以登录途 虎APP进入" + "“我的-待安装订单”进行预约安装。若您在服务过程中有任何疑问，请致电400-111-8868向途虎咨 询。");
        smsLogs.setProvince("北京");
        smsLogs.setOperatorId(1);
        smsLogs.setFee(3);
        request.add(new IndexRequest(index, type, "21").source(mapper.writeValueAsString(smsLogs), XContentType.JSON));

        smsLogs.setMobile("13700000001");
        smsLogs.setProvince("上海");
        smsLogs.setSmsContent("【途虎养车】亲爱的刘红先生/女士，您在途虎购买的货品(单号TH1234526)已 到指定安装店多日，" + "现需与您确认订单的安装情况，请点击链接按实际情况选择（此链接有效期为72H）。您也可以登录途 虎APP进入" + "“我的-待安装订单”进行预约安装。若您在服务过程中有任何疑问，请致电400-111-8868向途虎咨 询。");
        request.add(new IndexRequest(index, type, "22").source(mapper.writeValueAsString(smsLogs), XContentType.JSON));


        // -------------------------------------------------------------------------------------------------------------------

        SmsLogs smsLogs1 = new SmsLogs();
        smsLogs1.setMobile("13100000000");
        smsLogs1.setCorpName("盒马鲜生");
        smsLogs1.setCreateDate(new Date());
        smsLogs1.setSendDate(new Date());
        smsLogs1.setIpAddr("10.126.2.9");
        smsLogs1.setLongCode("10660000988");
        smsLogs1.setReplyTotal(15);
        smsLogs1.setState(0);
        smsLogs1.setSmsContent("【盒马】您尾号12345678的订单已开始配送，请在您指定的时间收货不要走开 哦~配送员：" + "刘三，电话：13800000000");
        smsLogs1.setProvince("北京");
        smsLogs1.setOperatorId(2);
        smsLogs1.setFee(5);
        request.add(new IndexRequest(index, type, "23").source(mapper.writeValueAsString(smsLogs1), XContentType.JSON));

        smsLogs1.setMobile("18600000001");
        smsLogs1.setProvince("上海");
        smsLogs1.setSmsContent("【盒马】您尾号7775678的订单已开始配送，请在您指定的时间收货不要走开 哦~配送员：" + "王五，电话：13800000001");
        request.add(new IndexRequest(index, type, "24").source(mapper.writeValueAsString(smsLogs1), XContentType.JSON));

        // -------------------------------------------------------------------------------------------------------------------

        SmsLogs smsLogs2 = new SmsLogs();
        smsLogs2.setMobile("15300000000");
        smsLogs2.setCorpName("滴滴打车");
        smsLogs2.setCreateDate(new Date());
        smsLogs2.setSendDate(new Date());
        smsLogs2.setIpAddr("10.126.2.8");
        smsLogs2.setLongCode("10660000988");
        smsLogs2.setReplyTotal(50);
        smsLogs2.setState(1);
        smsLogs2.setSmsContent("【滴滴单车平台】专属限时福利！青桔/小蓝月卡立享5折，特惠畅骑30天。" + "戳 https://xxxxxx退订TD");
        smsLogs2.setProvince("上海");
        smsLogs2.setOperatorId(3);
        smsLogs2.setFee(7);
        request.add(new IndexRequest(index, type, "25").source(mapper.writeValueAsString(smsLogs2), XContentType.JSON));

        smsLogs2.setMobile("18000000001");
        smsLogs2.setProvince("武汉");
        smsLogs2.setSmsContent("【滴滴单车平台】专属限时福利！青桔/小蓝月卡立享5折，特惠畅骑30天。" + "戳 https://xxxxxx退订TD");
        request.add(new IndexRequest(index, type, "26").source(mapper.writeValueAsString(smsLogs2), XContentType.JSON));


        // -------------------------------------------------------------------------------------------------------------------

        SmsLogs smsLogs3 = new SmsLogs();
        smsLogs3.setMobile("13900000000");
        smsLogs3.setCorpName("招商银行");
        smsLogs3.setCreateDate(new Date());
        smsLogs3.setSendDate(new Date());
        smsLogs3.setIpAddr("10.126.2.8");
        smsLogs3.setLongCode("10690000988");
        smsLogs3.setReplyTotal(50);
        smsLogs3.setState(0);
        smsLogs3.setSmsContent("【招商银行】尊贵的李四先生,恭喜您获得华为P30 Pro抽奖资格,还可领100 元打" + "车红包,仅限1天");
        smsLogs3.setProvince("上海");
        smsLogs3.setOperatorId(1);
        smsLogs3.setFee(8);
        request.add(new IndexRequest(index, type, "27").source(mapper.writeValueAsString(smsLogs3), XContentType.JSON));

        smsLogs3.setMobile("13990000001");
        smsLogs3.setProvince("武汉");
        smsLogs3.setSmsContent("【招商银行】尊贵的李四先生,恭喜您获得华为P30 Pro抽奖资格,还可领100 元打" + "车红包,仅限1天");
        request.add(new IndexRequest(index, type, "28").source(mapper.writeValueAsString(smsLogs3), XContentType.JSON));

        // -------------------------------------------------------------------------------------------------------------------

        SmsLogs smsLogs4 = new SmsLogs();
        smsLogs4.setMobile("13700000000");
        smsLogs4.setCorpName("中国平安保险有限公司");
        smsLogs4.setCreateDate(new Date());
        smsLogs4.setSendDate(new Date());
        smsLogs4.setIpAddr("10.126.2.8");
        smsLogs4.setLongCode("10690000998");
        smsLogs4.setReplyTotal(18);
        smsLogs4.setState(0);
        smsLogs4.setSmsContent("【中国平安】奋斗的时代，更需要健康的身体。中国平安为您提供多重健康保 障，在奋斗之路上为您保驾护航。退订请回复TD");
        smsLogs4.setProvince("武汉");
        smsLogs4.setOperatorId(1);
        smsLogs4.setFee(5);
        request.add(new IndexRequest(index, type, "29").source(mapper.writeValueAsString(smsLogs4), XContentType.JSON));

        smsLogs4.setMobile("13990000002");
        smsLogs4.setProvince("武汉");
        smsLogs4.setSmsContent("【招商银行】尊贵的王五先生,恭喜您获得iphone 56抽奖资格,还可领5 元打" + "车红包,仅限100天");
        request.add(new IndexRequest(index, type, "30").source(mapper.writeValueAsString(smsLogs4), XContentType.JSON));

        // -------------------------------------------------------------------------------------------------------------------


        SmsLogs smsLogs5 = new SmsLogs();
        smsLogs5.setMobile("13600000000");
        smsLogs5.setCorpName("中国移动");
        smsLogs5.setCreateDate(new Date());
        smsLogs5.setSendDate(new Date());
        smsLogs5.setIpAddr("10.126.2.8");
        smsLogs5.setLongCode("10650000998");
        smsLogs5.setReplyTotal(60);
        smsLogs5.setState(0);
        smsLogs5.setSmsContent("【北京移动】尊敬的客户137****0000，5月话费账单已送达您的139邮箱，" + "点击查看账单详情 http://y.10086.cn/; " + " 回Q关闭通知，关注“中国移动139邮箱”微信随时查账单【中国移动 139邮箱】");
        smsLogs5.setProvince("武汉");
        smsLogs5.setOperatorId(1);
        smsLogs5.setFee(4);
        request.add(new IndexRequest(index, type, "31").source(mapper.writeValueAsString(smsLogs5), XContentType.JSON));

        smsLogs5.setMobile("13990001234");
        smsLogs5.setProvince("山西");
        smsLogs5.setSmsContent("【北京移动】尊敬的客户137****1234，8月话费账单已送达您的126邮箱，\" + \"点击查看账单详情 http://y.10086.cn/; \" + \" 回Q关闭通知，关注“中国移动126邮箱”微信随时查账单【中国移动 126邮箱】");
        request.add(new IndexRequest(index, type, "32").source(mapper.writeValueAsString(smsLogs5), XContentType.JSON));
        // -------------------------------------------------------------------------------------------------------------------

        getClient().bulk(request, RequestOptions.DEFAULT);

        System.out.println("OK!");
    }

    /**
     * 使用term方式查询
     *
     * @throws IOException
     */
    @Test
    public void TermQuery() throws IOException {
        //获取request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        //指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(3);
        builder.query(QueryBuilders.termQuery("province", "北京"));
        request.source(builder);
        //执行查询
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        //获取_source的数据，并展示

        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit searchHit : hits) {
            Map<String, Object> result = searchHit.getSourceAsMap();
            System.out.println("使用term方式查询:" + JsonUtil.object2Json(result));
        }
    }

    /**
     * matchAll
     *
     * @throws IOException
     */
    @Test
    public void matchAllQuery() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        // 默认只显示10条数据，想查询更多，需要设置size
        //builder.size(20);
        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> result = hit.getSourceAsMap();
            System.out.println(result);
        }
    }

    /**
     * match查询
     *
     * @throws IOException
     */
    @Test
    public void matchQuery() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("smsContent", "收获安装"));
        // 默认只显示10条数据，想查询更多，需要设置size
        //builder.size(20);
        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> result = hit.getSourceAsMap();
            System.out.println(result);
        }
    }

    /**
     * booleanMatch查询
     *
     * @throws IOException
     */
    @Test
    public void booleanMatchQuery() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //Operator.AND   Operator.OR
        builder.query(QueryBuilders.matchQuery("smsContent", "中国 健康").operator(Operator.AND));
        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> result = hit.getSourceAsMap();
            System.out.println(result);
        }
    }

    /**
     * multiMatch查询 multi_match查询:查询为能在多个字段上反复执行相同查询提供了一种便捷方式
     *
     * @throws IOException
     */
    @Test
    public void MultiMatchQuery() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //多字段
        builder.query(QueryBuilders.multiMatchQuery("北京", "province", "smsContent"));
        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> result = hit.getSourceAsMap();
            System.out.println(result);
        }
    }

    @Test
    public void idSearch() throws IOException {
        GetRequest request = new GetRequest(index, type, "21");
        GetResponse response = getClient().get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsMap());
    }

    //ids查询  类似mysql中的 where id in(id1,id2.....)
    @Test
    public void idsSearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.idsQuery().addIds("21", "22", "23"));

        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void prefixSearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.prefixQuery("corpName", "途虎"));

        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    //wildcard查询
    //通配查询，查询时在字符串中指定通配符* 和 占位符 ？
    @Test
    public void wildCardSearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.wildcardQuery("corpName", "中国*"));

        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    //fuzzy查询
    //模糊查询    通过kibana可以看到，虽然把  "盒马鲜生"  写错了，但是也可以查到     prefix_length表示前多少个字符不能出错
    @Test
    public void fuzzySearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.fuzzyQuery("corpName", "盒马先生").prefixLength(2));

        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    //range查询
    //范围查询，只针对数值类型，对某一个field进行大于或者小于的指定
    @Test
    public void rangeSearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        //gt >  gte >=   lt <  lte <=
        builder.query(QueryBuilders.rangeQuery("fee").gt(5).lte(10));

        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    //regexp查询：通过正则表达式匹配内容
    //上文提到的 prefix、fuzzy、wildcard以及这里的regexp查询效率都比较低，要求效率比较高时尽量避免使用。
    @Test
    public void regexpSearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.regexpQuery("mobile", "180[0-9]{8}"));

        request.source(builder);
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    //深分页scroll查询
    @Test
    public void scrollSearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        //指定scroll信息
        //指定scroll生存时间
        request.scroll(TimeValue.MINUS_ONE);
        //指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(4);
        builder.sort("fee", SortOrder.DESC);
        builder.query(QueryBuilders.matchAllQuery());
        request.source(builder);

        //获取返回结构scrollId,source
        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        String scrollId = response.getScrollId();
        System.out.println("-----首页-----");
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

        while (true) {
            //循环 创建SearchScrollRequest   指定scrollId
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            //指定scrollId生存时间
            scrollRequest.scroll(TimeValue.MINUS_ONE);
            //执行查询获取返回结果
            SearchResponse searchResponse = getClient().scroll(scrollRequest, RequestOptions.DEFAULT);
            //判断是否查询到了数据，输出
            SearchHit[] hits = searchResponse.getHits().getHits();
            if (hits != null && hits.length > 0) {
                System.out.println("----下一页----");
                for (SearchHit hit : hits) {
                    System.out.println(hit.getSourceAsMap());
                }
            } else {
                //判断没有查到数据，退出循环
                System.out.println("----结束----");
                break;
            }
        }
        //创建clearScrollRequest
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        //指定scrollId
        clearScrollRequest.addScrollId(scrollId);
        //删除
        ClearScrollResponse clearScrollResponse = getClient().clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        System.out.println("删除scroll:" + clearScrollResponse.isSucceeded());
    }

    @Test
    public void deleteByQuery() throws IOException {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();
        deleteByQueryRequest.types(type);
        deleteByQueryRequest.setQuery(QueryBuilders.rangeQuery("fee").lt(4));
        BulkByScrollResponse response = getClient().deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        System.out.println("deleteByQuery response:" + response.toString());


    }

    /* 复合查询
             bool查询
     复合过滤器，将多个查询条件，以一定的逻辑组合在一起
     must 所有的条件，用must组合在一起，表示And的意思
     must_not  must_not中的条件全部都不匹配，表示Not的意思
     should  所有的条件用should组合在一起，表示 Or 的意思
     */
    @Test
    public void boolSearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.termQuery("province", "武汉"));
        boolQueryBuilder.should(QueryBuilders.termQuery("province", "北京"));

        boolQueryBuilder.mustNot(QueryBuilders.termQuery("operatorId", "2"));

        boolQueryBuilder.must(QueryBuilders.matchQuery("smsContent", "中国"));
        boolQueryBuilder.must(QueryBuilders.matchQuery("smsContent", "平安"));

        builder.query(boolQueryBuilder);
        request.source(builder);

        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void boostingSearch() throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoostingQueryBuilder boostingQueryBuilder = QueryBuilders.boostingQuery(
                QueryBuilders.matchQuery("smsContent", "收获安装"),
                QueryBuilders.matchQuery("smsContent", "刘红")
        ).negativeBoost(0.5f);
        builder.query(boostingQueryBuilder);
        request.source(builder);

        SearchResponse response = getClient().search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    public RestHighLevelClient getClient() {
        return esClient.restHighLevelClient();
    }
}
