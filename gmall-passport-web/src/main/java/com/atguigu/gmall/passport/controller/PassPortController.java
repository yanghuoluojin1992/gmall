package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.passport.util.JwtUtil;
import com.atguigu.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 认证模块
 * created by luogang on 2021-02-17 16:39
 */
@Controller
public class PassPortController {
    @Reference
    private UserService userService;
    @Value("${token.key}")
    private String key;

    @RequestMapping("index") //toLogin
    public String index(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl",originUrl);
        return "index";
    }


    @RequestMapping("login")
    public String login(UserInfo userInfo,HttpServletRequest request){
        if(userInfo != null){
            UserInfo user =  userService.login(userInfo);
            if(user !=null){
                String userId = user.getId();
                String nickName = user.getNickName();
                HashMap<String, Object> map = new HashMap<>();
                map.put("userId",userId);
                map.put("nickName",nickName);
                //获取服务ip  nginx配置 proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
                String salt = request.getHeader("X-forwarded-for");
                String token = JwtUtil.encode(key, map, salt);
                return token;
            }else{
                return "fail";
            }
        }
        return "fail";
    }

}
