package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * created by luogang on 2021-02-03 17:41
 */
@Data
public class BaseAttrInfo implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)//主键自增回显
    private String id;
    @Column
    private String attrName;
    @Column
    private String catalog3Id;

    @Transient //表示表中不存在的字段
    private List<BaseAttrValue> attrValueList;

}
