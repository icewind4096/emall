package com.windvalley.emall.controller.portal;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.ResponseCode;
import com.windvalley.emall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static com.windvalley.emall.controller.common.UserLogin.getUserDTOFromRedis;
import static com.windvalley.emall.controller.common.UserLogin.getUserDTOKey;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ICartService cartService;

    /**
     * 添加购物车
     * @param request
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("/update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, Integer productId, Integer count){
        ServerResponse serverResponse = checkAddParamater(productId, count);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.update(getUserIDFromSession(request), productId, count);
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(request));
        }

        return serverResponse;
    }

    /**
     * 删除购物车
     * @param request
     * @param productIds
     * @return
     */
    @RequestMapping("/delete.do")
    @ResponseBody
    public ServerResponse delete(HttpServletRequest request, String productIds) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.delete(getUserIDFromSession(request), productIds);
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(request));
        }

        return serverResponse;
    }

    /**
     * 用户购物车列表
     * @param request
     * @return
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest request) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return cartService.getListByUserId(getUserIDFromSession(request));
    }

    /**
     * 用户购物车全选
     * @param request
     * @return
     */
    @RequestMapping("/checkall.do")
    @ResponseBody
    public ServerResponse checkAll(HttpServletRequest request) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.checkAll(getUserIDFromSession(request));
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(request));
        }
        return serverResponse;
    }

    /**
     * 用户购物车全反选
     * @param request
     * @return
     */
    @RequestMapping("/uncheckall.do")
    @ResponseBody
    public ServerResponse unCheckAll(HttpServletRequest request) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.unCheckAll(getUserIDFromSession(request));
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(request));
        }
        return serverResponse;
    }

    /**
     * 购物车产品选中
     * @param request
     * @param productId
     * @return
     */
    @RequestMapping("/check.do")
    @ResponseBody
    public ServerResponse check(HttpServletRequest request, Integer productId) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.check(getUserIDFromSession(request), productId);
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(request));
        }
        return serverResponse;
    }

    /**
     * 购物车产品不选中
     * @param request
     * @param productId
     * @return
     */
    @RequestMapping("/uncheck.do")
    @ResponseBody
    public ServerResponse unCheck(HttpServletRequest request, Integer productId) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.unCheck(getUserIDFromSession(request), productId);
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(request));
        }
        return serverResponse;
    }

    /**
     * 得到购物车中全部商品数量
     * @param request
     * @return
     */
    @RequestMapping("/getcartproductnumber.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductNumber(HttpServletRequest request){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return ServerResponse.createBySuccess(0);
        }
        return cartService.getCartProductNumber(getUserIDFromSession(request));
    }


    private ServerResponse checkAddParamater(Integer productId, Integer count) {
        if (productId == null || count == null){
            return ServerResponse.createByError("添加购物车->参数错误");
        } else {
            if (count > 0){
                return ServerResponse.createBySuccess();
            } else {
                return ServerResponse.createByError("添加购物车->商品数量必须大于0");
            }
        }
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