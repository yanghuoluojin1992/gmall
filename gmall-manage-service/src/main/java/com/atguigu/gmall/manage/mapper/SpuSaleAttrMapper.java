package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * created by luogang on 2021-02-04 16:44
 */
public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListBySkuId(String skuId, String spuId);
}
