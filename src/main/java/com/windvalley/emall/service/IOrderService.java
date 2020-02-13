package com.windvalley.emall.service;

import com.windvalley.emall.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String uploadFilePath);

    ServerResponse processAlipayBack(Map<String, String> map);

    ServerResponse<Boolean> orderpaied(Integer userId, Long orderNo);
}
