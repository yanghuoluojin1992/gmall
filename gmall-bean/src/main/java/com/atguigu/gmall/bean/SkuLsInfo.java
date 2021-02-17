package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 将elastaicsearch gmll/SkuInfo 字段映射到实体类中
 * created by luogang on 2021-02-13 20:30
 */
@Data
public class SkuLsInfo implements Serializable {
    private String id;
    private BigDecimal price;
    private String skuName;
    private String catalog3Id;
    private String skuDefaultImg;
    private List<SkuLsAttrValue> skuAttrValueList;
    private Long hotScore = 0L;

}
