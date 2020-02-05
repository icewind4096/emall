package com.windvalley.emall.service;

import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.CartDTO;

public interface ICartService {
    ServerResponse update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartDTO> getListByUserId(Integer userId);

    ServerResponse delete(Integer userId, String productIds);

    ServerResponse checkAll(Integer userId);

    ServerResponse unCheckAll(Integer userId);

    ServerResponse check(Integer userId, Integer productId);

    ServerResponse unCheck(Integer userId, Integer productId);

    ServerResponse<Integer> getCartProductNumber(Integer userId);
}
