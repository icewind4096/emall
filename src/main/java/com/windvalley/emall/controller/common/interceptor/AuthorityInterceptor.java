package com.windvalley.emall.controller.common.interceptor;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.service.IUserService;
import com.windvalley.emall.util.CookieUtil;
import com.windvalley.emall.util.JsonUtil;
import com.windvalley.emall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthorityInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle");

        HandlerMethod handlerMethod = (HandlerMethod) o;

        if (requestIsLogin(handlerMethod) == true){
            return true;
        }

        UserDTO userDTO = getUserDTOByToken(getTokenFromRequest(httpServletRequest));
        if (userDTO == null ){
            //富文本返回有特定的格式要求
            if (isRichTextUpload(handlerMethod) == true){
                responseMessageByJSON(httpServletResponse, getErrMessageMapByRichText(false, "拦截器拦截:用户未登录"));
            } else {
                responseMessageByJSON(httpServletResponse, "拦截器拦截:用户未登录");
            }
            return false;
        }

        if (userIsManager(userDTO) == false){
            if (isRichTextUpload(handlerMethod) == true) {
                responseMessageByJSON(httpServletResponse, getErrMessageMapByRichText(false, "拦截器拦截:用户非管理员，无权限操作"));
            } else {
                responseMessageByJSON(httpServletResponse, "拦截器拦截:用户非管理员，无权限操作");
            }
            return false;
        }

        return true;
    }

    private Map getErrMessageMapByRichText(Boolean success, String message) {
        Map map = new HashMap();
        map.put("success", success);
        map.put("msg", message);
        return map;
    }

    private void responseMessage(HttpServletResponse response, String contendType, Map map) throws IOException {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType(contendType);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(JsonUtil.object2String(map));
        printWriter.flush();
        printWriter.close();
    }

    private void responseMessageByJSON(HttpServletResponse response, Map map) throws IOException {
        responseMessage(response,"application/json;charset=UTF-8", map);
    }

    private boolean isRichTextUpload(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().getName().equals("richUpload")
                && handlerMethod.getBean().getClass().getSimpleName().equals("ProductManagerController");
    }

    private boolean requestIsLogin(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().getName().equals("login")
            && handlerMethod.getBean().getClass().getSimpleName().equals("UserManagerController");
    }

    private void responseMessageByJSON(HttpServletResponse response, String message) throws IOException {
        responseMessage(response,"application/json;charset=UTF-8", message);
    }

    private void responseMessage(HttpServletResponse response, String contendType, String message) throws IOException {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType(contendType);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(JsonUtil.object2String(ServerResponse.createByError(message)));
        printWriter.flush();
        printWriter.close();
    }

    private boolean userIsManager(UserDTO userDTO) {
        if (userDTO != null){
            return userService.isManagerRole(userDTO.getUsername()).isSuccess();
        }
        return false;
    }

    private UserDTO getUserDTOByToken(String token) {
        if (token != null) {
            return JsonUtil.string2Object(RedisShardedPoolUtil.get(token), UserDTO.class);
        }
        return null;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return CookieUtil.readLoginToken(request);
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
