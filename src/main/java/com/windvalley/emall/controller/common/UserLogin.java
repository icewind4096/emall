package com.windvalley.emall.controller.common;

import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.util.CookieUtil;
import com.windvalley.emall.util.JsonUtil;
import com.windvalley.emall.util.RedisShardedPoolUtil;

import javax.servlet.http.HttpServletRequest;

public class UserLogin {
    public static String getUserDTOKey(HttpServletRequest request) {
        return CookieUtil.readLoginToken(request);
    }

    public static UserDTO getUserDTOFromRedis(String key) {
        return JsonUtil.string2Object(RedisShardedPoolUtil.get(key), UserDTO.class);
    }
}
