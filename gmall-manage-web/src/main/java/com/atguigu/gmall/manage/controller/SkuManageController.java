package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * created by luogang on 2021-02-05 16:10
 */
@RestController
@CrossOrigin
public class SkuManageController {
    @Reference
    private ManageService manageService;
    @Reference
    private ListService listService;

    //获取spu图片  http://localhost:8082/spuImageList?spuId=80
    @RequestMapping("spuImageList")
    public List<SpuImage> getspuImageList(SpuImage spuImage){
        return manageService.getSpuImageList(spuImage);
    }

    //获取spu销售属性和属性值 http://localhost:8082/spuSaleAttrList?spuId=80
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){
        return manageService.getSpuSaleAttrList(spuId);
    }

    //获取平台属性和属性值 http://localhost:8082/attrInfoList?catalog3Id=61 在ManageController里有 需要重写实现类

    //http://localhost:8082/saveSkuInfo
    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo){
        if(skuInfo!=null){
            manageService.saveSkuInfo(skuInfo);
        }
    }

    //商品从数据库 保存到 es中，商品上架
    @RequestMapping("onSale")
    public void onSale(@RequestParam String skuInfoId){
        SkuInfo skuInfo = manageService.getSkuInfo(skuInfoId);

        SkuLsInfo skuLsInfo = new SkuLsInfo();
        BeanUtils.copyProperties(skuInfo,skuLsInfo);
        listService.saveSkuLsInfo(skuLsInfo);
    }

}
