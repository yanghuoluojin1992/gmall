package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

/**
 * created by luogang on 2021-02-03 17:52
 */
public interface ManageService {
    List<BaseCatalog1> getCatalog1();
    List<BaseCatalog2> getCatalog2(String catalog1Id);
    List<BaseCatalog3> getCatalog3(String catalog2Id);
    List<BaseAttrInfo> getAttrInfo(String catalog3Id);

    void saveAttrInfo(BaseAttrInfo attrInfo);

    /**
     * 根据平台属性id 查询平台属性值集合  注意业务逻辑，先返回一个BaseAttrInfo对象，在从对象取出集合
     * @param attrId
     * @return
     */
    BaseAttrInfo getAttrInfoById(String attrId);
}
