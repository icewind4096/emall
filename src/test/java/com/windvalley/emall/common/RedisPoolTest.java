package com.windvalley.emall.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class RedisPoolTest {
    @Test
    public void TestJedis() {
        Jedis jedis = RedisPool.getJedis();
        jedis.set("name", "wangj");
        RedisPool.returnResource(jedis);
    }
}
