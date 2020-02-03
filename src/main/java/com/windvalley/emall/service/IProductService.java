package com.windvalley.emall.service;

import com.github.pagehelper.PageInfo;
import com.windvalley.emall.common.ServerResponse;
import com.windvalley.emall.dto.ProductDTO;

public interface IProductService {
    ServerResponse save(ProductDTO productDTO);

    ServerResponse update(ProductDTO productDTO);

    ServerResponse updateStatus(Integer productId, Integer status);

    ServerResponse<ProductDTO> getDetailByManager(Integer productId);

    ServerResponse<PageInfo> getProductListByManager(Integer pageNumber, Integer pageSize);

    ServerResponse<PageInfo> search(String productName, Integer productId, Integer pageNumber, Integer pageSize);

    ServerResponse<ProductDTO> getDetailByUser(Integer productId);

    ServerResponse<PageInfo> getProductByKeyWordAndCategoryId(String keyWord, Integer categoryId, Integer pageNumber, Integer pageSize, String orderBy);
}
