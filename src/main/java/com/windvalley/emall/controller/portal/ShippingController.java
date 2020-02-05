package com.windvalley.emall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.Const;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.converter.Shipping2ShippingDTO;
import com.windvalley.emall.dto.ShippingDTO;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.ResponseCode;
import com.windvalley.emall.form.ShippingForm;
import com.windvalley.emall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private IShippingService shippingService;

    /**
     * 添加收货地址
     * @param httpSession
     * @param shippingForm
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession httpSession, ShippingForm shippingForm){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        ShippingDTO shippingDTO = Shipping2ShippingDTO.convert(shippingForm);
        shippingDTO.setUserId(getUserIDFromSession(httpSession));

        return shippingService.add(shippingDTO);
    }

    /**
     * 删除收货地址
     * @param httpSession
     * @param shippingId
     * @return
     */
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpSession httpSession, Integer shippingId){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return shippingService.del(getUserIDFromSession(httpSession), shippingId);
    }

    /**
     * 修改收货地址
     * @param httpSession
     * @param shippingForm
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession httpSession, ShippingForm shippingForm){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        ShippingDTO shippingDTO = Shipping2ShippingDTO.convert(shippingForm);
        shippingDTO.setUserId(getUserIDFromSession(httpSession));

        return shippingService.update(shippingDTO);
    }

    /**
     * 收货地址详细
     * @param httpSession
     * @param shippingId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ShippingDTO> detail(HttpSession httpSession, Integer shippingId){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return shippingService.detail(getUserIDFromSession(httpSession), shippingId);
    }

    /**
     * 收货地址列表
     * @param httpSession
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession httpSession
                                        ,@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
                                        ,@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        ServerResponse serverResponse = checkUserCanOperate(httpSession);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return shippingService.listShipping(getUserIDFromSession(httpSession), pageNumber, pageSize);
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
