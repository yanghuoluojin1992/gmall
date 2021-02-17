package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsAttrValue;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;

import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by luogang on 2021-02-13 20:39
 */
@Service
public class ListServiceImpl implements ListService {
    @Autowired
    private JestClient jestClient;
    @Autowired
    private RedisUtil redisUtil; //当前启动类加扫描

    public static final String ES_INDEX = "gmall";
    public static final String ES_TYPE = "SkuInfo";

    @Override
    public void saveSkuLsInfo(SkuLsInfo skuLsInfo) {
        //定义dsl类型，保存数据到es
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        String query = makeQueryStringForSearch(skuLsParams);
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult = null;
        try {
             searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SkuLsResult skuLsResult = makeResultForSearch(searchResult, skuLsParams);
        return skuLsResult;
    }

    @Override
    public void incrHotScore(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        //此处数据类型为zset 排名
        Double hotScore = jedis.zincrby("hotScore", 1, "skuId:" + skuId);
        if(hotScore%10 == 0){
            updateHotScore(skuId,Math.round(hotScore));
        }

    }
    //es中更新热点数
    private void updateHotScore(String skuId, long hotScore) {
        String updStr = "{\n" +
                "  \"doc\": {\n" +
                "    \"hotScore\":"+hotScore+"\n" +
                "  }\n" +
                "}\n";
        Update update = new Update.Builder(updStr).index(ES_INDEX).type(ES_TYPE).id(skuId).build();
        try {
            jestClient.execute(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将搜索结果封装成SkuLsResult类型:
    private SkuLsResult makeResultForSearch(SearchResult searchResult, SkuLsParams skuLsParams) {
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        List<SkuLsInfo> skuLsInfoList = new ArrayList<>();
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            //封装List<SkuLsInfo> skuLsInfoList
            SkuLsInfo skuLsInfo = hit.source;
            //设skuName高亮
            Map<String, List<String>> highlight = hit.highlight;
            if (highlight!=null && highlight.size()>0){
                List<String> list = highlight.get("skuName");//list为一个字符串
                skuLsInfo.setSkuName(list.get(0));
            }
            skuLsInfoList.add(skuLsInfo);
       }
        SkuLsResult skuLsResult = new SkuLsResult();
        skuLsResult.setSkuLsInfoList(skuLsInfoList);
        Long total = searchResult.getTotal();
        skuLsResult.setTotal(total);
        //TODO: totalPages
        Long totalPages = (total + skuLsParams.getPageSize()-1)/skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPages);
        //通过聚合aggregations封装List<String> attrValueIdList;
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        if(groupby_attr!= null){
            ArrayList<String> arrayList = new ArrayList<>();
            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                String valueId = bucket.getKey();
                arrayList.add(valueId);
            }
            skuLsResult.setAttrValueIdList(arrayList);
        }

        return skuLsResult;
    }
    //将搜索参数解析成动态DSL语句
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        //创建查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //设置keyword：skuinfoname查询条件
        if(!StringUtils.isEmpty(skuLsParams.getKeyword())){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",skuLsParams.getKeyword());
            boolQueryBuilder.must(matchQueryBuilder);
            //设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlight(highlightBuilder);
        }
        //设置三级分类查询条件
        if(!StringUtils.isEmpty(skuLsParams.getCatalog3Id())){
            TermsQueryBuilder terms = new TermsQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(terms);
        }
        //设置属性值查询条件
        String[] valueIds = skuLsParams.getValueId();
        if(valueIds!=null && valueIds.length>0){
            for (String valueId : valueIds) {
                TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termsQueryBuilder);
            }

        }

        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页 TODO: from
        int from = (skuLsParams.getPageNo()-1)*skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        int pageSize = skuLsParams.getPageSize();
        searchSourceBuilder.size(pageSize);
        //设置排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        //设置聚合，分组排序
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);
        String query = searchSourceBuilder.toString();
        System.out.println("query: "+query);
        return query;
    }
}
