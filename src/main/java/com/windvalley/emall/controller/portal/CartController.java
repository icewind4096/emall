package com.windvalley.emall.controller.portal;

import com.windvalley.emall.common.Const;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.ResponseCode;
import com.windvalley.emall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ICartService cartService;

    /**
     * 添加购物车
     * @param httpSession
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("/update.do")
    @ResponseBody
    public ServerResponse update(HttpSession httpSession, Integer productId, Integer count){
        ServerResponse serverResponse = checkAddParamater(productId, count);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.update(getUserIDFromSession(httpSession), productId, count);
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(httpSession));
        }

        return serverResponse;
    }

    /**
     * 删除购物车
     * @param httpSession
     * @param productIds
     * @return
     */
    @RequestMapping("/delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession httpSession, String productIds) {
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.delete(getUserIDFromSession(httpSession), productIds);
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(httpSession));
        }

        return serverResponse;
    }

    /**
     * 用户购物车列表
     * @param httpSession
     * @return
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse list(HttpSession httpSession) {
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return cartService.getListByUserId(getUserIDFromSession(httpSession));
    }

    /**
     * 用户购物车全选
     * @param httpSession
     * @return
     */
    @RequestMapping("/checkall.do")
    @ResponseBody
    public ServerResponse checkAll(HttpSession httpSession) {
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.checkAll(getUserIDFromSession(httpSession));
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(httpSession));
        }
        return serverResponse;
    }

    /**
     * 用户购物车全反选
     * @param httpSession
     * @return
     */
    @RequestMapping("/uncheckall.do")
    @ResponseBody
    public ServerResponse unCheckAll(HttpSession httpSession) {
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.unCheckAll(getUserIDFromSession(httpSession));
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(httpSession));
        }
        return serverResponse;
    }

    /**
     * 购物车产品选中
     * @param httpSession
     * @param productId
     * @return
     */
    @RequestMapping("/check.do")
    @ResponseBody
    public ServerResponse check(HttpSession httpSession, Integer productId) {
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.check(getUserIDFromSession(httpSession), productId);
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(httpSession));
        }
        return serverResponse;
    }

    /**
     * 购物车产品不选中
     * @param httpSession
     * @param productId
     * @return
     */
    @RequestMapping("/uncheck.do")
    @ResponseBody
    public ServerResponse unCheck(HttpSession httpSession, Integer productId) {
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        serverResponse = cartService.unCheck(getUserIDFromSession(httpSession), productId);
        if (serverResponse.isSuccess()){
            return cartService.getListByUserId(getUserIDFromSession(httpSession));
        }
        return serverResponse;
    }

    /**
     * 得到购物车中全部商品数量
     * @param httpSession
     * @return
     */
    @RequestMapping("/getcartproductnumber.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductNumber(HttpSession httpSession){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return ServerResponse.createBySuccess(0);
        }
        return cartService.getCartProductNumber(getUserIDFromSession(httpSession));
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
