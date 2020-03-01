package com.windvalley.emall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    private static final String COOKIE_DOMAIN = "emall.com";
    private static final String COOKIE_NAME = "emall_login_token";

    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(COOKIE_NAME, token);
    //避免一定程度的跨站攻击。防止脚本攻击,禁止了通过脚本获取cookie信息,浏览器不会将其发送给任何第三方
        cookie.setHttpOnly(true);
    //跨域共享cookie tomcat8.5版本默认使用的是rfc6265规则,前置.不需要，否则产生异常
        cookie.setDomain(COOKIE_DOMAIN);
    //cookie共享的范围
        cookie.setPath("/");
    //-1 永久有效 单位是秒, 如果不设置maxage，则cookie不会写入硬盘，只会在当前页面有效
    //1月有效
        cookie.setMaxAge(60 * 60 * 24 *30);
        response.addCookie(cookie);
    }

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie: cookies){
                log.info("Read cookie name:{} value:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie: cookies){
                log.info("Read cookie name:{} value:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)){
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                //0 删除cookie
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
