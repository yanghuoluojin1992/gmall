package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * created by luogang on 2021-02-09 17:15
 */
@Controller
public class ItemController {
    @Reference
    private ManageService manageService;
    @Reference
    private ListService listService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, HttpServletRequest request){
        //根据skuId查询skuinfo，根据skuId查询spuImages，放入skuInfo
        SkuInfo skuInfo =  manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo",skuInfo);

        //根据spuId查询销售属性和销售属性值集合,并联合skuId获取选中的销售属性和值
        //销售属性回显并锁定
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListBySkuId(skuInfo.getId(),skuInfo.getSpuId());
        request.setAttribute("spuSaleAttrList",spuSaleAttrList);

        //根据spuId查询销售属性值id集合，根据用户选择的销售属性值id组成“{"156|168":"2",157|159:3}” 在从前台js判断skuid是否存在，进行页面跳转
        //点击销售属性实现切换
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleValueList(skuInfo.getSpuId());
        String key = "";
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            if(key.length()>0){
                key += "|";
            }
            key += skuSaleAttrValue.getSaleAttrValueId();
            //当前元素的skuId和下一个元素skuId不等时或当取到集合最后一个元素，停止拼接，将key放入map中，并清空key
            if((i+1)==skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i+1).getSkuId())
                    ){
                map.put(key,skuSaleAttrValue.getSkuId());
                key = "";
            }
        }
        //fastJson将map转换成json字符串
        String valuesSkuJson  = JSON.toJSONString(map);
        request.setAttribute("valuesSkuJson",valuesSkuJson);

        //调用热点统计服务
        listService.incrHotScore(skuId);
        return  "item";
    }
}
