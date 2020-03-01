package com.windvalley.emall.controller.portal;

import com.github.pagehelper.PageInfo;
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

import javax.servlet.http.HttpServletRequest;

import static com.windvalley.emall.controller.common.UserLogin.getUserDTOFromRedis;
import static com.windvalley.emall.controller.common.UserLogin.getUserDTOKey;

@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private IShippingService shippingService;

    /**
     * 添加收货地址
     * @param request
     * @param shippingForm
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest request, ShippingForm shippingForm){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        ShippingDTO shippingDTO = Shipping2ShippingDTO.convert(shippingForm);
        shippingDTO.setUserId(getUserIDFromSession(request));

        return shippingService.add(shippingDTO);
    }

    /**
     * 删除收货地址
     * @param request
     * @param shippingId
     * @return
     */
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpServletRequest request, Integer shippingId){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return shippingService.del(getUserIDFromSession(request), shippingId);
    }

    /**
     * 修改收货地址
     * @param request
     * @param shippingForm
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, ShippingForm shippingForm){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        ShippingDTO shippingDTO = Shipping2ShippingDTO.convert(shippingForm);
        shippingDTO.setUserId(getUserIDFromSession(request));

        return shippingService.update(shippingDTO);
    }

    /**
     * 收货地址详细
     * @param request
     * @param shippingId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ShippingDTO> detail(HttpServletRequest request, Integer shippingId){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return shippingService.detail(getUserIDFromSession(request), shippingId);
    }

    /**
     * 收货地址列表
     * @param request
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request
                                        ,@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
                                        ,@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return shippingService.listShipping(getUserIDFromSession(request), pageNumber, pageSize);
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
