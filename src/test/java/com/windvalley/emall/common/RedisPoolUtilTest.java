package com.windvalley.emall.common;

import com.windvalley.emall.util.RedisPoolUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class RedisPoolUtilTest {
    @Test
    public void TestJedis() {
        RedisPoolUtil.set("name", "renyp");
        Assert.assertEquals("OK", RedisPoolUtil.setExpire("age", "10", 100));
        RedisPoolUtil.expire("name", 100);
        RedisPoolUtil.get("age");
        RedisPoolUtil.del("name");
    }
}
