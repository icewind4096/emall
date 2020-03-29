package com.windvalley.emall.common;

import com.windvalley.emall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool {
    //ShardedJedis 连接池
    private static ShardedJedisPool pool;

    //Redis IP地址
    private static String redis_1_ip = PropertiesUtil.getProperty("redis1.ip");

    //Redis 端口号
    private static Integer redis_1_port = PropertiesUtil.getProperty("redis1.port", 6379);

    //Redis IP地址
    private static String redis_2_ip = PropertiesUtil.getProperty("redis2.ip");

    //Redis 端口号
    private static Integer redis_2_port = PropertiesUtil.getProperty("redis2.port", 6380);

    //Redis 超时时间
    private static Integer timeout = PropertiesUtil.getProperty("redis.timeout", 1000);

    //最大连接数
    private static Integer maxTotal = PropertiesUtil.getProperty("redis.max.total", 10);

    //连接池空闲时，最大jedis实例个数
    private static Integer maxIdel = PropertiesUtil.getProperty("redis.max.idle", 10);

    //连接池空闲时，最小jedis实例个数
    private static Integer minIdel = PropertiesUtil.getProperty("redis.min.idle", 2);

    //在从连接池取出一个jedis实例时，是否进行验证操作
    private static Boolean testOnBorrow = PropertiesUtil.getProperty("redis.test.borrow", true);

    //归还一个jedis实例到连接池时，是否进行验证操作
    //输出了2个方法returnBrokenResource和returnResource,用程序处理无效连接,此处就无须判断，可以提高连接效率
    private static Boolean testOnReturn = PropertiesUtil.getProperty("redis.test.return", false);

    //连接池中无可用连接时，是否阻塞
    private static Boolean blockWhenExhausted = PropertiesUtil.getProperty("redis.blockWhenExhausted", true);

    static {
        initPool();
    }

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdel);
        config.setMinIdle(minIdel);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(blockWhenExhausted);

//        JedisShardInfo jedisShardInfo_1 = new JedisShardInfo(redis_1_ip, redis_1_port, timeout);
        JedisShardInfo jedisShardInfo_2 = new JedisShardInfo(redis_2_ip, redis_2_port, timeout);

        List<JedisShardInfo> jedisShardInfos = new ArrayList<>();
//        jedisShardInfos.add(jedisShardInfo_1);
        jedisShardInfos.add(jedisShardInfo_2);

        pool = new ShardedJedisPool(config, jedisShardInfos, Hashing.MURMUR_HASH, ShardedJedis.DEFAULT_KEY_TAG_PATTERN);
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis shardedJedis){
        pool.returnResource(shardedJedis);
    }

    public static void returnBrokenResource(ShardedJedis shardedJedis){
        pool.returnBrokenResource(shardedJedis);
    }

}
