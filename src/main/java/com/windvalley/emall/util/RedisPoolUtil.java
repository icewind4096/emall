package com.windvalley.emall.util;

import com.windvalley.emall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisPoolUtil {
    public static String set(String key, String value){
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            String result = jedis.set(key, value);
            RedisPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis set-> key:{} value:{} error:", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static String setExpire(String key, String value, Integer expireTime){
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            String result = jedis.setex(key, expireTime, value);
            RedisPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis setexpire-> key:{} value:{} expireTime:{} error:", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    /**
     * 设置key的有效期单位秒
     * @param key  键
     * @param expireTime 失效时间
     * @return 1:成功
     *         0:失败
     */
    public static Long expire(String key, Integer expireTime){
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            Long result = jedis.expire(key, expireTime);
            RedisPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis expire-> key:{} expireTime:{} error:", key, expireTime, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static String get(String key){
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            String result = jedis.get(key);
            RedisPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis get-> key:{} error:", key, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static Long del(String key){
        Jedis jedis = null;
        try {
            jedis = RedisPool.getJedis();
            Long result = jedis.del(key);
            RedisPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis del-> key:{} error:", key, e);
            RedisPool.returnBrokenResource(jedis);
            return null;
        }
    }
}
