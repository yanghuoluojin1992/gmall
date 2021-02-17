package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;

/**
 * created by luogang on 2021-02-13 20:35
 */
public interface ListService {
    /**
     *将数据库中的skuInfo保存到es中
     */
    void saveSkuLsInfo(SkuLsInfo skuLsInfo);

    /**
     * ES根据搜索条件返回搜索结果
     * @return
     */
    SkuLsResult search(SkuLsParams skuLsParams);

    /**
     * 根据商品详情的浏览数，增加SkuLsInfo热点
     * @param skuId
     */
    void incrHotScore(String skuId);
}
