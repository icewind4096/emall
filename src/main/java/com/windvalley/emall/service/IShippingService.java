package com.windvalley.emall.service;

import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.ShippingDTO;

public interface IShippingService {
    ServerResponse add(ShippingDTO shippingDTO);

    ServerResponse del(Integer userId, Integer shippingId);

    ServerResponse update(ShippingDTO shippingDTO);

    ServerResponse<ShippingDTO> detail(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> listShipping(Integer userId, Integer pageNumber, Integer pageSize);
}
