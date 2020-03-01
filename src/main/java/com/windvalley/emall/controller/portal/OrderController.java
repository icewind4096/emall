package com.windvalley.emall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.pagehelper.PageInfo;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.windvalley.emall.controller.common.UserLogin.getUserDTOFromRedis;
import static com.windvalley.emall.controller.common.UserLogin.getUserDTOKey;

@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {
    @Autowired
    IOrderService orderService;

    /**
     * 新建订单
     * @param request
     * @param shippingId
     * @return
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest request, Integer shippingId){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.create(getUserIDFromSession(request), shippingId);
    }

    /**
     * 取消订单
     * @param request
     * @param orderId
     * @return
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest request, Long orderId){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.cancel(getUserIDFromSession(request), orderId);
    }

    /**
     * 得到购物车中已选择商品
     * @param request
     * @return
     */
    @RequestMapping("getordercartproduct.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest request){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.getOrderCartProduct(getUserIDFromSession(request));
    }

    /**
     * 得到订单详情
     * @param request
     * @param orderId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpServletRequest request, Long orderId) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.detailByOrderIdAndUserId(getUserIDFromSession(request), orderId);
    }

    /**
     * 用户订单列表
     * @param request
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request
                                        ,@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
                                        ,@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.getlistByUserId(getUserIDFromSession(request), pageNumber, pageSize);
    }

    /**
     * 订单支付
     * @param request
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(Long orderNo, HttpServletRequest request){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        String path = request.getSession().getServletContext().getRealPath(getWebAppUploadDir());
        return orderService.pay(getUserIDFromSession(request), orderNo, path);
    }

    /**
     * 检查订单是否支付
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping("orderpaied.do")
    @ResponseBody
    public ServerResponse<Boolean> orderpaied(HttpServletRequest request, Long orderNo){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.orderpaied(getUserIDFromSession(request), orderNo);
    }

    /**
     * 支付宝支付成功以后的回调入口
     * @param request
     * @return
     */
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

    private Integer getUserIDFromSession(HttpServletRequest request) {
        UserDTO userDTO = getUserDTOFromRedis(getUserDTOKey(request));
        return userDTO.getId();
    }

    private ServerResponse checkUserCanOperate(HttpServletRequest request) {
        if (checkUserLogin(request) == false){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要登录");
        }
        return ServerResponse.createBySuccess();
    }

    private boolean checkUserLogin(HttpServletRequest request) {
        return getUserDTOFromRedis(getUserDTOKey(request)) != null;
    }
}
