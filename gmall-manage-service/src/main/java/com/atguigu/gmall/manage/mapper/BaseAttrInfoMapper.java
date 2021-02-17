package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * created by luogang on 2021-02-03 17:49
 */
public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    List<BaseAttrInfo> getAttrInfo(String catalog3Id);

    List<BaseAttrInfo> getAttrInfoByValueIds(@Param("attrValueIds") String attrValueIds);
}
