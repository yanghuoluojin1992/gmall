package com.atguigu.gmall.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * created by luogang on 2021-02-10 14:59
 */
public class RedisUtil {
    private JedisPool jedisPool;
    public void initJedisPool(String host,int port,int database,String password){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(200);
        config.setMaxWaitMillis(10*1000);
        //设置最小预留连接数
        config.setMinIdle(10);
        // 如果到最大数，设置等待
        config.setBlockWhenExhausted(true);
        // 在获取连接时，检查是否有效
        config.setTestOnBorrow(true);

        jedisPool = new JedisPool(config,host,port,10*1000,password);
    }
    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}
