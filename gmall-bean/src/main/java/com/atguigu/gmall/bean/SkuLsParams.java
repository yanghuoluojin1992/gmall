package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * created by luogang on 2021-02-13 22:38
 */
@Data
public class SkuLsParams implements Serializable {
    String  keyword;

    String catalog3Id;

    String[] valueId;

    int pageNo=1;

    int pageSize=20;

}
