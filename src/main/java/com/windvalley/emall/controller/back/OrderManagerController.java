package com.windvalley.emall.controller.back;

import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.OrderDTO;
import com.windvalley.emall.dto.UserDTO;
import com.windvalley.emall.enums.ResponseCode;
import com.windvalley.emall.service.IOrderService;
import com.windvalley.emall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static com.windvalley.emall.controller.common.UserLogin.getUserDTOFromRedis;
import static com.windvalley.emall.controller.common.UserLogin.getUserDTOKey;

@Controller
@RequestMapping("/manager/order")
public class OrderManagerController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private IUserService userService;

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request
            , @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.getlistByManager(pageNumber, pageSize);
    }

    /**
     * 得到订单详情
     * @param request
     * @param orderId
     * @return
     */
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<OrderDTO> detail(HttpServletRequest request, Long orderId){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.detailByOrderId(orderId);
    }

    /**
     * 订单查询
     * todo现在只做了精确查找orderId
     * @param request
     * @param orderId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpServletRequest request, Long orderId
                                          ,@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
                                          ,@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.search(orderId, pageNumber, pageSize);
    }

    @RequestMapping("/delivery.do")
    @ResponseBody
    public ServerResponse<String> delivery(HttpServletRequest request, Long orderId){
        ServerResponse serverResponse = checkUserCanOperate(request);
        if (serverResponse.isSuccess() == false){
            return serverResponse;
        }

        return orderService.delivery(orderId);
    }

    private ServerResponse checkUserCanOperate(HttpServletRequest request) {
        if (checkUserLogin(request) == false){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要登录");
        }

        if (checkUserIsManager(request) == false){
            return ServerResponse.createByError("无管理员权限");
        }

        return ServerResponse.createBySuccess();
    }

    private boolean checkUserIsManager(HttpServletRequest request) {
        UserDTO userDTO = getUserDTOFromRedis(getUserDTOKey(request));
        return userService.isManagerRole(userDTO.getUsername()).isSuccess();
    }

    private boolean checkUserLogin(HttpServletRequest request) {
        return getUserDTOFromRedis(getUserDTOKey(request)) != null;
    }
}
