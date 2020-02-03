package com.windvalley.emall.converter;

import com.windvalley.emall.dto.ProductDTO;
import com.windvalley.emall.pojo.Product;
import org.springframework.beans.BeanUtils;

public class ProductDTO2Product {
    public static Product convert(ProductDTO productDTO){
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        return product;
    }
}
