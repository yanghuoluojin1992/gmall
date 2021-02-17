package com.atguigu.gmall.usermanage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.usermanage.mapper.UserAddressMapper;
import com.atguigu.gmall.usermanage.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * created by luogang on 2021-02-02 13:16
 */
@Service // com.alibaba.dubbo.config.annotation.Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<UserInfo> findAll() {
        List<UserInfo> list = userMapper.selectAll();
        return list;
    }

    @Override
    public List<UserAddress> getUserAddressById(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        List<UserAddress> addressList = userAddressMapper.select(userAddress);
        return addressList;
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        UserInfo user = userMapper.selectOne(userInfo);
        if(user != null){
            //将用户存入redis中
            Jedis jedis = redisUtil.getJedis();
            String key = "user:"+user.getId()+":userInfo";
            jedis.setex(key,7*24*60*60, JSON.toJSONString(user));
            return user;
        }
        return null;
    }
}
