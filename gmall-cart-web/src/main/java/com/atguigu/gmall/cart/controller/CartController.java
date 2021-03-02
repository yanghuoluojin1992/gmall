package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.cart.config.CartCookieHandler;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.ManageService;
import com.atguigu.gmall.web.util.LoginRequire;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * created by luogang on 2021-02-18 18:46
 */
@Controller
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private CartCookieHandler cartCookieHandler;
    @Reference()
    private ManageService manageService;

    @LoginRequire(autoRediect = false)//需要认证，
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response){
        String skuId = request.getParameter("skuId");
        String skuNum = request.getParameter("skuNum");
        String userId = (String) request.getAttribute("userId");
        if (!StringUtils.isEmpty(userId)){
            //用户已登录， CartInfo保存到redis+mysql
            cartService.addToCart(userId,skuId,skuNum);
        }else{
            //用户未登录，CartInfo保存到cookie
            cartCookieHandler.addToCart(request,response,skuId,skuNum,userId);

        }
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuNum",skuNum);
        request.setAttribute("skuInfo",skuInfo);
        return "success";
    }

    @LoginRequire(autoRediect = false)//需要认证，区分是否登录
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfoList;
        if (!StringUtils.isEmpty(userId)) {
            //登录时cookie如果有数据，合并
            List<CartInfo> cartInfoListCookie = cartCookieHandler.getCartList(request);
            if (cartInfoListCookie != null && cartInfoListCookie.size() > 0) {
                cartInfoList = cartService.mergeCartList(userId, cartInfoListCookie);
                cartCookieHandler.delCartListCookie(request, response);
            } else {
                //cookie没数据，从redis，数据库中取数据
                cartInfoList = cartService.getCartList(userId);
            }
        } else {
            //未登录从cookie中取数据
            cartInfoList = cartCookieHandler.getCartList(request);
        }
        request.setAttribute("cartInfoList", cartInfoList);
        return "cartList";
    }

    //根据购物车勾选状态，修改redis和cookie中的cartInfo isChecked属性
    @LoginRequire(autoRediect = false)
    @RequestMapping("checkCart")
    public void checkCart(HttpServletRequest request,HttpServletResponse response){
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");
        String userId = (String) request.getAttribute("userId");
        if (!StringUtils.isEmpty(userId)){
            //用户已登录 修改redis
            cartService.checkCart(userId,skuId,isChecked);
        }else{
            //未登录  修改cookie
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }
    }
    @LoginRequire(autoRediect = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request,HttpServletResponse response){
        //要解决用户在未登录且购物车中有商品的情况下，直接点击结算。
        //所以不能直接跳到结算页面，要让用户强制登录后，检查cookie并进行合并后再重定向到结算页面
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfoListCookie = cartCookieHandler.getCartList(request);
        if (cartInfoListCookie != null && cartInfoListCookie.size() > 0) {
            List<CartInfo> cartInfoList = cartService.mergeCartList(userId, cartInfoListCookie);
            cartCookieHandler.delCartListCookie(request, response);
        }
        return "redirect:http://order.gmall.com/trade";
    }
}
