package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * created by luogang on 2021-02-03 18:11
 */
@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;


    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        return manageService.getCatalog1();
    }

    //http://localhost:8082/getCatalog2?catalog1Id=2
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(@RequestParam String catalog1Id){
        return manageService.getCatalog2(catalog1Id);
    }

    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(@RequestParam String catalog2Id){
        return manageService.getCatalog3(catalog2Id);
    }

    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> getAttrInfo(@RequestParam String catalog3Id){
        return manageService.getAttrInfo(catalog3Id);
    }
    //http://localhost:8082/saveAttrInfo   //保存和修改
    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo attrInfo){
        manageService.saveAttrInfo(attrInfo);
    }

    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
       BaseAttrInfo attrInfo =  manageService.getAttrInfoById(attrId);
       return attrInfo.getAttrValueList();
    }
}
