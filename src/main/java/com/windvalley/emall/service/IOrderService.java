package com.windvalley.emall.service;

import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.OrderDTO;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String uploadFilePath);

    ServerResponse processAlipayBack(Map<String, String> map);

    ServerResponse<Boolean> orderpaied(Integer userId, Long orderNo);

    ServerResponse create(Integer userId, Integer shippingId);

    ServerResponse cancel(Integer userId, Long orderId);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderDTO> detailByOrderIdAndUserId(Integer userId, Long orderId);

    ServerResponse<PageInfo> getlistByUserId(Integer userId, Integer pageNumber, Integer pageSize);

    ServerResponse<PageInfo> getlistByManager(Integer pageNumber, Integer pageSize);

    ServerResponse<OrderDTO> detailByOrderId(Long orderId);

    ServerResponse<PageInfo> search(Long orderId, Integer pageNumber, Integer pageSize);

    ServerResponse<String> delivery(Long orderId);
}
