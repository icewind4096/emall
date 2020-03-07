package com.windvalley.emall.controller.common;

import com.windvalley.emall.common.Const;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.util.CookieUtil;
import com.windvalley.emall.util.JsonUtil;
import com.windvalley.emall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TokenExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)){
            if (JsonUtil.string2Object(RedisShardedPoolUtil.get(loginToken), UserDTO.class) != null){
                RedisShardedPoolUtil.expire(loginToken, Const.REDIS_EXPIRE_TIME);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
