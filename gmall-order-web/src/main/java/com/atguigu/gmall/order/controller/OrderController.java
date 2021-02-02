package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * created by luogang on 2021-02-02 13:33
 */
@Controller
public class OrderController {
    @Reference
    private UserService userService;

    @RequestMapping("trade")
    public String toIndex(){
        return "index";
    }

    @ResponseBody
    @RequestMapping("getUserAddress") //?id = 1
    public List<UserAddress> getUserAddress(String id){
        List<UserAddress> userAddressList = userService.getUserAddressById(id);
        return userAddressList;
    }



}
