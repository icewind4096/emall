package com.windvalley.emall.converter;

import com.windvalley.emall.dto.ShippingDTO;
import com.windvalley.emall.pojo.Shipping;
import org.springframework.beans.BeanUtils;

public class ShippingDTO2Shipping {
    public static Shipping convert(ShippingDTO shippingDTO){
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(shippingDTO, shipping);
        return shipping;
    }
}
