package com.windvalley.emall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Author:
 */
@Slf4j
public class PropertiesUtil {
    private static Properties props;

    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            log.error("配置文件读取异常",e);
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key, String defaultValue){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }



    public static Integer getProperty(String key, Integer defaultValue){
        String value = props.getProperty(key.trim()).trim();
        if(com.alipay.api.internal.util.StringUtils.isNumeric(value)){
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    public static Boolean getProperty(String key, boolean defaultValue) {
        String value = props.getProperty(key.trim()).trim();
        if (StringUtils.isNoneBlank(value)){
            return "true".equals(value.toLowerCase());
        }
        return defaultValue;
    }
}
