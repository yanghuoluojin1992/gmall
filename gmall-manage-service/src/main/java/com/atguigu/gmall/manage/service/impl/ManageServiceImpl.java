package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.manage.constant.ManageConstant;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * created by luogang on 2021-02-03 17:53
 */
@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2);
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfo(String catalog3Id) {
       /* BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        return baseAttrInfoMapper.select(baseAttrInfo);*/
        //需要重写，获取平台属性和平台属性值
        return baseAttrInfoMapper.getAttrInfo(catalog3Id);

    }

    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo attrInfo) {
        if(attrInfo.getId()!=null){
            baseAttrInfoMapper.updateByPrimaryKeySelective(attrInfo);
        }else{
            //保存平台属性
            baseAttrInfoMapper.insertSelective(attrInfo);
        }

        //修改平台属性值之前先删除已有的平台属性值
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrInfo.getId());//注意实体类设置主键自增回显主键
        baseAttrValueMapper.delete(baseAttrValue);
        //保存平台属性值
        List<BaseAttrValue> attrValueList = attrInfo.getAttrValueList();
        if(attrValueList!=null&&attrValueList.size()>0){
            for (BaseAttrValue attrValue : attrValueList) {
                //int flag = 1/0;创建异常也可以验证事务
                //attrValue.setId("144"); //选一个数据库存在的id，让sql报错，验证事务
                attrValue.setAttrId(attrInfo.getId());
                baseAttrValueMapper.insertSelective(attrValue);
            }
        }

    }

    @Override
    public BaseAttrInfo getAttrInfoById(String attrId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> attrValueList = baseAttrValueMapper.select(baseAttrValue);

        BaseAttrInfo attrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        attrInfo.setAttrValueList(attrValueList);
        return attrInfo;
    }

    @Override
    public List<SpuInfo> getSpuList(SpuInfo spuInfo) {
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {

        return baseSaleAttrMapper.selectAll();

    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insertSelective(spuInfo);
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList!=null &&spuImageList.size()>0){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList!=null &&spuSaleAttrList.size()>0){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(spuSaleAttrValueList!=null && spuSaleAttrValueList.size()>0){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrList(spuId);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList!=null && skuImageList.size()>0){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(skuAttrValueList!=null && skuAttrValueList.size()>0){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList!=null && skuSaleAttrValueList.size()>0){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }
        }


    }

    @Override
    //缓存击穿：当redis中某个热点key不存在时，大量请求直接访问mysql数据库
    public SkuInfo getSkuInfo(String skuId) {

        return getSkuInfoRedisson(skuId);
    }
    //使用redisson加锁解决缓存击穿问题
    private SkuInfo getSkuInfoRedisson(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        RLock lock = null;
        try {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://192.168.78.128:6379")
                    .setPassword("123456");
            RedissonClient redisson = Redisson.create(config);
            lock = redisson.getLock("myLock");
            lock.lock(10, TimeUnit.SECONDS);
            //业务代码
            jedis = redisUtil.getJedis();
            String skuKey = ManageConstant.SKUKEY_PREFIX+ skuId +ManageConstant.SKUKEY_SUFFIX;
            String skuInfoStr = jedis.get(skuKey);
            if (!StringUtils.isEmpty(skuInfoStr)){
                skuInfo = JSON.parseObject(skuInfoStr, SkuInfo.class);
                return skuInfo;
            }else{
                skuInfo = getSkuInfoDB(skuId);
                String skuInfoJson = JSON.toJSONString(skuInfo);
                jedis.setex(skuKey,ManageConstant.SKUKEY_TIMEOUT,skuInfoJson);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                jedis.close();
            }
            if (lock!=null){
                lock.unlock();
            }
        }
        return getSkuInfoDB(skuId);
    }

    //使用redis分布式锁，解决缓存击穿问题
    private SkuInfo getSkuInfoRedisLock(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String skuKey = ManageConstant.SKUKEY_PREFIX+ skuId +ManageConstant.SKUKEY_SUFFIX;
            String skuInfoStr = jedis.get(skuKey);
            if (StringUtils.isEmpty(skuInfoStr)){
                //没有数据 ,需要加锁！取出完数据，还要放入缓存中，下次直接从缓存中取得即可！
                String skuLockKey = ManageConstant.SKUKEY_PREFIX+ skuId +ManageConstant.SKULOCK_SUFFIX;
                //设置锁，当skuLockKey不存在时，设置成功返回ok
                String result = jedis.set(skuLockKey, "LOCK-OK", "NX", "PX", ManageConstant.SKULOCK_EXPIRE_PX);
                if (result.equals("OK")){
                    System.out.println("未命中缓存，redis加锁成功！");
                    skuInfo = getSkuInfoDB(skuId);
                    String skuInfoJson = JSON.toJSONString(skuInfo);
                    jedis.setex(skuKey,ManageConstant.SKUKEY_TIMEOUT,skuInfoJson);
                    jedis.del(skuLockKey);
                    return skuInfo;
                }else{
                    //其他线程为获取到锁，先等一会，再从数据库查寻
                    Thread.sleep(1000);
                    return getSkuInfoDB(skuId);
                }
            }else{
                //有数据，取缓存数据
                skuInfo = JSON.parseObject(skuInfoStr, SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    //从redis中取数据，如果没有，从db中取数据
    private SkuInfo getSkuInfoRedis(String skuId) {
        //将skuInfo转为json字符串，存在redis中  key: sku:skuId:Info  通过redis集群和try-catch解决redis宕机问题
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String skuKey = ManageConstant.SKUKEY_PREFIX+ skuId +ManageConstant.SKUKEY_SUFFIX;
            String skuInfoStr = jedis.get(skuKey);
            if (!StringUtils.isEmpty(skuInfoStr)){
                skuInfo = JSON.parseObject(skuInfoStr, SkuInfo.class);
                return skuInfo;
            }else{
                skuInfo = getSkuInfoDB(skuId);
                String skuInfoJson = JSON.toJSONString(skuInfo);
                jedis.setex(skuKey,ManageConstant.SKUKEY_TIMEOUT,skuInfoJson);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return getSkuInfoDB(skuId);
    }

    //使用数据库查询getSkuInfo
    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        //添加setSkuImageList
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuInfo.getId());
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);
        //添加skuAttrValueList
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListBySkuId(String skuId, String spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrListBySkuId(skuId,spuId);
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleValueList(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleValueList(spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfo(List<String> attrValueIdList) {
        String attrValueIds = StringUtils.join(attrValueIdList.toArray(), ",");
        List<BaseAttrInfo> list = baseAttrInfoMapper.getAttrInfoByValueIds(attrValueIds);
        return list;
    }


}
