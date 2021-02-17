package com.atguigu.gmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created by luogang on 2021-02-10 15:00
 */
@Configuration //相当于一个xml配置文件
public class RedisConfig {
    //:disable 代表从配置文件取不到值时的默认值disable
    @Value("${spring.redis.host:disable}")
    private String host;
    @Value("${spring.redis.port:0}")
    private int port;
    @Value("${spring.redis.database:0}")
    private int database;
    @Value("${spring.redis.password:0}")
    private String password;

    @Bean //相当于一个bean标签，交由spring管理对象，之后可以用@autowired注入RedisUtil
    public RedisUtil getRedisUtil(){
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initJedisPool(host,port,database,password);
        return redisUtil;
    }
}
