package com.windvalley.emall.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.ShardedJedis;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class RedisShardedPoolTest {
    @Test
    public void TestSharedePool() {
        ShardedJedis shardedJedis = RedisShardedPool.getJedis();
        shardedJedis.set("name", "wangj");
        shardedJedis.set("age", "38");
        shardedJedis.set("sex", "male");
        shardedJedis.set("address", "china");
        for (int i = 0; i < 20; i ++){
            shardedJedis.set("key" + i, "value" + i);
        }
        RedisShardedPool.returnResource(shardedJedis);
    }
}
