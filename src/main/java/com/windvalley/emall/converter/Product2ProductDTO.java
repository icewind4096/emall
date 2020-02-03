package com.windvalley.emall.converter;

import com.windvalley.emall.dto.ProductDTO;
import com.windvalley.emall.form.ProductFrom;
import com.windvalley.emall.pojo.Product;
import org.springframework.beans.BeanUtils;

public class Product2ProductDTO {
    public static ProductDTO convert(ProductFrom productFrom){
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(productFrom, productDTO);
        return productDTO;
    }

    public static ProductDTO convert(Product product){
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }
}
