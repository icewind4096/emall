package com.windvalley.emall.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.windvalley.emall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Slf4j
public class JsonUtil {
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        //对象的所有字段，全部序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        //取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        //忽略对象无Get方法时，转JSON的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //所有日期格式统一为yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));

        //忽略在JSON字符串中存在，但是在JAVA对象中不存在对应属性的情况
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String object2String(T object){
        if (object == null){
            return null;
        }

        if (object instanceof String){
            return (String) object;
        } else {
            try {
                return objectMapper.writeValueAsString(object);
            } catch (Exception e) {
                log.error("Object2String: Err", e);
                return null;
            }
        }
    }

    /**
     * 输出转换字符串， 格式化输出
     * @param object
     * @param <T>
     * @return
     */
    public static <T> String object2StringPretty(T object){
        if (object == null){
            return null;
        }

        if (object instanceof String){
            return (String) object;
        } else {
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } catch (Exception e) {
                log.error("Object2String: Err", e);
                return null;
            }
        }
   }

   public static <T> T string2Object(String value, Class<T> clazz){
        if (StringUtils.isEmpty(value) || clazz == null){
            return null;
        }

        if (clazz.equals(String.class)){
            return (T) value;
        } else {
            try {
                return objectMapper.readValue(value, clazz);
            } catch (IOException e) {
                log.error("String2Object: Err", e);
                return null;
            }
        }
   }

   public static <T> T string2Object(String value, TypeReference<T> typeReference){
       if (StringUtils.isEmpty(value) || typeReference == null){
           return null;
       }

       try {
           if (typeReference.getType().equals(String.class)){
               return (T)value;
           } else {
               return objectMapper.readValue(value, typeReference);
           }
       } catch (IOException e) {
           log.error("String2Object: Err", e);
           return null;
       }
   }

    public static <T> T string2Object(String value, Class<?> collectionClass, Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(value, javaType);
        } catch (IOException e) {
            log.error("String2Object: Err", e);
            return null;
        }
    }
}
