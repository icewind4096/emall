package com.windvalley.emall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.windvalley.emall.common.Const;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.AlipayCallBackResponse;
import com.windvalley.emall.enums.ResponseCode;
import com.windvalley.emall.pay.alipay.PayConfig;
import com.windvalley.emall.service.IOrderService;
import com.windvalley.emall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {
    @Autowired
    IOrderService orderService;

    /**
     * 订单支付
     * @param httpSession
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession httpSession, Long orderNo, HttpServletRequest request){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        String path = request.getSession().getServletContext().getRealPath(getWebAppUploadDir());
        return orderService.pay(getUserIDFromSession(httpSession), orderNo, path);
    }

    @RequestMapping("orderpaied.do")
    @ResponseBody
    public ServerResponse<Boolean> orderpaied(HttpSession httpSession, Long orderNo){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.orderpaied(getUserIDFromSession(httpSession), orderNo);
    }

    @RequestMapping(value = "alipaycallback.do", method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> map = getMapsFromRequestPara(request.getParameterMap());

        log.info("signtype:{}, sign:{}, tradeStatus:{}, 参数:{}", map.get("sign_type"), map.get("sign"), map.get("trade_status"), map.toString());
        if (checkSingnValid(map)) {
            if (orderService.processAlipayBack(map).isSuccess()){
                return AlipayCallBackResponse.SUCCESS.getDescript();
            } else {
                return AlipayCallBackResponse.ERROR.getDescript();
            }
        } else {
            return ServerResponse.createByError("非法请求，回调参数不正确");
        }
    }

    private boolean checkSingnValid(Map<String, String> map) {
        //文档要求，必须移除signtype和sign项，才可以验证
        map.remove("sign_type");

        try {
            return AlipaySignature.rsaCheckV2(map, PayConfig.ALIPAY_PUBLIC_KEY, PayConfig.CHARSET, PayConfig.SIGN_TYPE);
        } catch (AlipayApiException e) {
            log.error("检验Alipay回调参数错误", e);
            return false;
        }
    }

    private Map<String, String> getMapsFromRequestPara(Map<String, String[]> parameterMap) {
        Map<String, String> map = new HashMap<>();
        for (Iterator iterator = parameterMap.keySet().iterator(); iterator.hasNext();){
            String name = (String) iterator.next();
            String[] values = (String[]) parameterMap.get(name);
            map.put(name, getStringByValues(values));
        }
        return map;
    }

    private String getStringByValues(String[] values) {
        String value = "";
        for (int i = 0; i < values.length; i ++){
            value = value + values[i];
            if (i != values.length - 1){
                value = value + ",";
            }
        }
        return value;
    }

    private String getWebAppUploadDir() {
        return PropertiesUtil.getProperty("webapp.upload.dir");
    }

    private Integer getUserIDFromSession(HttpSession httpSession) {
        UserDTO userDTO = (UserDTO) httpSession.getAttribute(Const.CURRENT_USER);
        return userDTO.getId();
    }

    private ServerResponse checkUserCanOperate(HttpSession httpSession) {
        if (checkUserLogin(httpSession) == false){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要登录");
        }
        return ServerResponse.createBySuccess();
    }

    private boolean checkUserLogin(HttpSession httpSession) {
        return (UserDTO) httpSession.getAttribute(Const.CURRENT_USER) != null;
    }
}
