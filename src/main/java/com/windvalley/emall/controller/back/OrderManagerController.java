package com.windvalley.emall.controller.back;

import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.OrderDTO;
import com.windvalley.emall.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manager/order")
public class OrderManagerController {
    @Autowired
    private IOrderService orderService;

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request
            , @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber
            , @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
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
        return orderService.search(orderId, pageNumber, pageSize);
    }

    @RequestMapping("/delivery.do")
    @ResponseBody
    public ServerResponse<String> delivery(HttpServletRequest request, Long orderId){
        return orderService.delivery(orderId);
    }
}
