package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * created by luogang on 2021-02-14 0:09
 */
@Controller
public class ListController {
    @Reference
    private ListService listService;
    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
    public String listData(SkuLsParams skuLsParams, HttpServletRequest request){
        skuLsParams.setPageSize(2);
        SkuLsResult searchResult = listService.search(skuLsParams);
        List<SkuLsInfo> skuLsInfoList = searchResult.getSkuLsInfoList();
        if(skuLsInfoList==null||skuLsInfoList.size()==0){//商品不存在，跳转首页
            return "redirect:http://www.gmall.com/";
        }
        request.setAttribute("skuLsInfoList",skuLsInfoList);
        //设置平台属性名和属性值
        List<String> attrValueIdList = searchResult.getAttrValueIdList();
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrInfo(attrValueIdList);
        request.setAttribute("baseAttrInfoList",baseAttrInfoList);
        //当点击平台属性值时，获取当前页面的url后缀参数，拼接valueId跳转页面
        String paramUrl = makeParamUrl(skuLsParams);
        request.setAttribute("paramUrl",paramUrl);
        //存放面包屑的集合
        List<BaseAttrValue> selectedValueList = new ArrayList<>();
        //当平台属性值追加到url中时，baseAttrInfoList应该去掉valueId相同的平台属性  使用itco 迭代器删除集合属性
        for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo attrInfo =  iterator.next();
            List<BaseAttrValue> attrValueList = attrInfo.getAttrValueList();
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    String[] valueIds = skuLsParams.getValueId();
                    if(valueIds!=null && valueIds.length>0){
                        for (String valueId : valueIds) {
                            if(valueId.equals(baseAttrValue.getId())){
                                iterator.remove();
                                //面包屑添加数据 "内存：3G"
                                BaseAttrValue selectedAttrValue = new BaseAttrValue();
                                selectedAttrValue.setValueName(attrInfo.getAttrName()+":"+baseAttrValue.getValueName());

                                //删除选中的面包屑
                                String newParamUrl = makeParamUrl(skuLsParams,valueId);
                                selectedAttrValue.setParamUrl(newParamUrl);
                                selectedValueList.add(selectedAttrValue);
                            }
                        }
                    }
                }
            }

        request.setAttribute("selectedValueList",selectedValueList);
        request.setAttribute("keyword",skuLsParams.getKeyword());
        //设置分页
        request.setAttribute("totalPages",searchResult.getTotalPages());
        request.setAttribute("pageNo",skuLsParams.getPageNo());

        return "list";

    }
    //获取当前的url后面的参数
    //th:href="'/list.html?'+${paramUrl}+'&valueId='+${attrValue.id}"
    //http://list.gmall.com/list.html?keyword=小米&catalog3Id=61&valueId=12&valueId=14
    private String makeParamUrl(SkuLsParams skuLsParams,String...excludeValueIds) {
        String paramUrl = "";
        if(!StringUtils.isEmpty(skuLsParams.getKeyword())){
            paramUrl+="keyword="+skuLsParams.getKeyword();
        }
        if(!StringUtils.isEmpty(skuLsParams.getCatalog3Id())){
            if(paramUrl.length()>0){
                paramUrl+="&";
            }
            paramUrl+="catalog3Id="+skuLsParams.getCatalog3Id();
        }
        if(skuLsParams.getValueId()!=null && skuLsParams.getValueId().length>0){
            for (String valueId : skuLsParams.getValueId()) {
                //去掉面包屑
                if(excludeValueIds!=null&&excludeValueIds.length>0){
                    if(valueId.equals(excludeValueIds[0])){
                        continue;//跳出本次循环，进入下次循环
                    }
                }
                if(paramUrl.length()>0){
                    paramUrl+="&";
                }
                paramUrl+="valueId="+valueId;
            }
        }
        return paramUrl;
    }
}
