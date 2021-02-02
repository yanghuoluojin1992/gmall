package com.atguigu.gmall.usermanage.controller;

import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * created by luogang on 2021-02-02 13:19
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("findAll")
    public List<UserInfo> findAll(){
        return userService.findAll();
    }
}
