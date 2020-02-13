package com.windvalley.emall.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class QRCodeUtilTest {
    @Test
    public void encode() {
        QRCodeUtil.encode("abc", "C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\emall_war\\upload\\", "123", QRCodeUtil.FORMATNAME);
        File file = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\emall_war\\upload\\", "123.png");
        Assert.assertEquals(true, file.exists());
    }
}
