GET _search/

POST _analyze
{
"analyzer": "ik_max_word",
"text":"武汉科技大学"
}

#创建person索引
PUT /person
{
"settings": {
"number_of_replicas": 1,
"number_of_shards": 5
}
}

#查看索引
GET /person

#删除索引
DELETE /person


#创建book索引
PUT /book
{
"settings": {
"number_of_replicas": 1,
"number_of_shards": 5
},
"mappings": {
"novel":{
"properties":{
"name":{
"type":"text",
"analyzer": "ik_max_word",
"index": true,
"store": false
},
"author":{
"type":"keyword"
},
"count":{
"type":"long"
},
"onSale":{
"type":"date",
"format":"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"

        },
        "description":{
          "type":"text",
          "analyzer": "ik_max_word"
        }
      }
    }
}
}

#添加文档，自动生成id
POST /book/novel
{
"name": "盘龙",
"author": "我吃西红柿",
"count": "1000000",
"onSale": "2000-01-01",
"description": "测试test111111111"
}

#添加文档，手动指定id
PUT /book/novel/1
{
"name": "红楼梦",
"author": "曹雪芹",
"count": "1000000",
"onSale": "1900-01-01",
"description": "四大名著"
}
#修改文档，手动指定id-不推荐-覆盖式修改
PUT /book/novel/1
{
"name": "红楼梦",
"author": "曹雪芹",
"count": "1000000",
"onSale": "1900-01-01",
"description": "四大名著"
}

#DOC修改文档，手动指定id-推荐-单独式修改
POST /book/novel/1/_update
{
"doc":{
"count":"600"
}
}

#根据id删除文档
DELETE /book/novel/BzRqkIQBQ-gHiBqSltw8

GET /person


POST /sms-logs-index/sms-logs-type/_search
{
"from": 0,
"size": 20,
"query": {
"term": {
"province": {
"value": "北京"
}
}
}
}

POST /sms-logs-index/sms-logs-type/_search
{
"from": 0,
"size": 20,
"query": {
"terms": {
"province": [
"北京",
"山西",
"武汉"
]
}
}
}

#mach_all查询
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"match_all": {}
}
}


#mach查询
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"match": {
"smsContent": "收货安装"
}
}
}

#mach查询-布尔
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"match": {
"smsContent": {
"query": "中国 健康",
"operator": "and"
}
}
}
}


#multi_match查询:查询为能在多个字段上反复执行相同查询提供了一种便捷方式
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"multi_match": {
"query": "中国",
"fields": ["province","smsContent"]
}
}
}

#根据id查询
GET /sms-logs-index/sms-logs-type/21

#根据ids查询
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"ids":{
"values": ["21","22","23"]
}
}
}


#prefix查询 通过一个关键字去指定一个Field的前缀，从而查询到指定的文档
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"prefix":{
"corpName": {
"value": "途虎"
}
}
}
}

#fuzzy查询
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"fuzzy":{
"corpName": {
"value": "盒马先生",
"prefix_length": 2
}
}
}
}

#wildcard查询-通配符
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"wildcard":{
"corpName": {
"value": "中国??"
}
}
}
}
#range查询 范围查询，只针对数值类型，对某一个field进行大于或者小于的指定
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"range":{
"fee": {
"gte": 5,
"lte": 20
}
}
}
}

#regexp查询：通过正则表达式匹配内容
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"regexp":{
"mobile": "186[0-9]{8}"
}
}
}

#scroll查询：
POST /sms-logs-index/sms-logs-type/_search?scroll=1m
{
"query": {
"match_all": {}
},
"size": 20,
"sort": [
{
"fee": {
"order": "desc"
}
}
]
}

#scroll查询：第二页数据
POST /_search/scroll
{
"scroll_id":"DnF1ZXJ5VGhlbkZldGNoAwAAAAAAASYzFkpfYWE2X0xhVG82T3Nodkh4MnZQQ2cAAAAAAAEmNBZKX2FhNl9MYVRvNk9zaHZIeDJ2UENnAAAAAAABJjUWSl9hYTZfTGFUbzZPc2h2SHgydlBDZw==",
"scroll":"1m"
}

#bool查询
#查询省份是武汉或者北京
#运营商不是联通
#smsContent中包含中国和平安
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"bool": {
"should": [
{
"term": {
"province": {
"value": "北京"
}
}
},
{
"term": {
"province": {
"value": "武汉"
}
}
}
],
"must_not": [
{
"term": {
"operatorId": {
"value": "2"
}
}
}
],
"must": [
{
"match": {
"smsContent": "中国"
}
},
{
"match": {
"smsContent": "平安"
}
}
]
}
}
}

#filter查询
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"bool": {
"filter": [
{
"term":{
"corpName": "盒马鲜生"
}
},
{
"range":{
"fee": {
"lte":5
}
}
}
]
}
}
}

#highLight查询
POST /sms-logs-index/sms-logs-type/_search
{
"query": {
"match": {
"smsContent": "盒马"
}
},
"highlight": {
"fields": {
"smsContent": {}
},
"pre_tags": "<font color='red'>",
"post_tags": "</font>",
"fragment_size": 10
}
}

#聚合查询-去重计数
POST /sms-logs-index/sms-logs-type/_search
{
"aggs": {
"agg": {
"cardinality": {
"field": "province"
}
}
}
}

#聚合查询-数值方式范围统计
POST /sms-logs-index/sms-logs-type/_search
{
"aggs": {
"agg": {
"range": {
"field": "fee",
"ranges": [
{
"to": 5
},
{
"from": 5,
"to": 10
},
{
"from": 10
}
]
}
}
}
}

#聚合查询-时间方式范围统计
POST /sms-logs-index/sms-logs-type/_search
{
"aggs": {
"agg": {
"date_range": {
"field": "createDate",
"format": "yyyy",
"ranges": [
{
"to": 2000
},
{
"from": 2000
}
]
}
}
}
}

#聚合查询-ip方式范围统计
POST /sms-logs-index/sms-logs-type/_search
{
"aggs": {
"agg": {
"ip_range": {
"field": "ipAddr",
"ranges": [
{
"to": "10.126.2.8"
},
{
"from": "10.126.2.9"
}
]
}
}
}
}

#聚合查询-统计聚合查询
POST /sms-logs-index/sms-logs-type/_search
{
"aggs": {
"agg": {
"extended_stats": {
"field": "fee"
}
}
}
}

#经纬度查询geo_point
PUT /map
{
"settings": {
"number_of_replicas": 1,
"number_of_shards": 5
},
"mappings": {
"map":{
"properties":{
"name":{
"type": "text"
},
"location":{
"type": "geo_point"
}
}
}
}
}

#添加数据
PUT /map/map/1
{
"name": "天安门",
"location":{
"lon": "116.404177",
"lat": "39.909652"
}
}

#添加数据
PUT /map/map/2
{
"name": "海淀公园",
"location":{
"lon": "116.298948",
"lat": "39.990523"
}
}


PUT /map/map/3
{
"name": "北京动物园",
"location":{
"lon": "116.341913",
"lat": "39.502905"
}
}


POST /map/map/_search
{
"query": {
"geo_distance": {
"location":
{
"lon": "116.341913",
"lat": "39.502905"
},
"distance":2000,
"distance_type":"arc"
}
}
}




























