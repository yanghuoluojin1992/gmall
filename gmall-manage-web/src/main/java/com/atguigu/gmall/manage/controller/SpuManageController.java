package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.ManageService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * created by luogang on 2021-02-04 17:13
 */

@RestController
@CrossOrigin
public class SpuManageController {
    @Reference
    private ManageService manageService;


    //spuList?catalog3
    @RequestMapping("spuList")
    public List<SpuInfo> getSpuList( SpuInfo spuInfo){ //实体类封装传过来的id 加了@requestBody 400？
        List<SpuInfo> list = manageService.getSpuList(spuInfo);
        return list;
    }

    //获取基本销售属性集合
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList(){
        return manageService.getBaseSaleAttrList();
    }

    //保存商品信息 saveSpuInfo  spuInfo/spuImage/spuSaleAttr/spuSaleAttrValue四张表保存数据
    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo){
        if(spuInfo != null){
             manageService.saveSpuInfo(spuInfo);
        }
    }



}
