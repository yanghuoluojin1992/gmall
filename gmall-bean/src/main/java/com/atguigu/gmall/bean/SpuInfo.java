package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * created by luogang on 2021-02-04 16:33
 */
@Data
public class SpuInfo implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String spuName;
    @Column
    private String description;
    @Column
    private String catalog3Id;
    @Column
    private String tmId;
    @Transient
    private List<SpuImage> spuImageList;
    @Transient
    private List<SpuSaleAttr> spuSaleAttrList;
}
