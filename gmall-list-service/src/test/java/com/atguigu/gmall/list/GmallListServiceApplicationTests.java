package com.atguigu.gmall.list;


import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)//有了@RunWith(SpringRunner.class)Autowired类才能实例化到spring容器中，自动注入才能生效，
@SpringBootTest
public class GmallListServiceApplicationTests {
    @Autowired
    private JestClient jestClient;

    @Test
   public void contextLoads() throws IOException {
        //定义dsl语句
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"name\": \"湄公河行动\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        //定义dsl语句类型 查询 添加 --
        Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie_type_chn").build();
        // 添加  Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        //执行dsl语句
        SearchResult searchResult = jestClient.execute(search);
        List<SearchResult.Hit<Map, Void>> hits = searchResult.getHits(Map.class);//如果有实体类 可以写Entity.class
        for (SearchResult.Hit<Map, Void> hit : hits) {
            Map map = hit.source;
            System.out.println(map);
        }
    }

}
