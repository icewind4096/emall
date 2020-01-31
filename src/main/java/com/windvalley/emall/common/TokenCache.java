package com.windvalley.emall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenCache {
    private static LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                                                               .initialCapacity(1000)
                                                               .maximumSize(10000)
                                                               .expireAfterAccess(12, TimeUnit.HOURS)
                                                               .build(new CacheLoader<String, String>() {
                                                                   @Override
                                                                   public String load(String s) throws Exception {
                                                                       return "null";
                                                                   }
                                                               });
    public static void setKey(String key, String value){
        loadingCache.put(key, value);
    }

    public static String getKey(String key){
        try {
            String value = null;
            value = loadingCache.get(key);
            if ("null".equalsIgnoreCase(value)){
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            log.error("localCache Error: get ->", e);
        }
        return null;
    }
}
