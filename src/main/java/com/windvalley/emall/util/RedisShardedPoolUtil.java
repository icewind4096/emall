package com.windvalley.emall.util;

import com.windvalley.emall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedPoolUtil {
    public static String set(String key, String value){
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            String result = jedis.set(key, value);
            RedisShardedPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis set-> key:{} value:{} error:", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static String setExpire(String key, String value, Integer expireTime){
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            String result = jedis.setex(key, expireTime, value);
            RedisShardedPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis setexpire-> key:{} value:{} expireTime:{} error:", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
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
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            Long result = jedis.expire(key, expireTime);
            RedisShardedPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis expire-> key:{} expireTime:{} error:", key, expireTime, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static String get(String key){
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            String result = jedis.get(key);
            RedisShardedPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis get-> key:{} error:", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
    }

    public static Long del(String key){
        ShardedJedis jedis = null;
        try {
            jedis = RedisShardedPool.getJedis();
            Long result = jedis.del(key);
            RedisShardedPool.returnResource(jedis);
            return result;
        }catch (Exception e){
            log.error("Redis del-> key:{} error:", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return null;
        }
    }
}
