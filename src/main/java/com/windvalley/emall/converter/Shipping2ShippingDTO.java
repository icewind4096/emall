package com.windvalley.emall.converter;

import com.windvalley.emall.dto.ShippingDTO;
import com.windvalley.emall.form.ShippingForm;
import com.windvalley.emall.pojo.Shipping;
import org.springframework.beans.BeanUtils;

public class Shipping2ShippingDTO {
    public static ShippingDTO convert(ShippingForm shippingForm){
        ShippingDTO shippingDTO = new ShippingDTO();
        BeanUtils.copyProperties(shippingForm, shippingDTO);
        return shippingDTO;
    }

    public static ShippingDTO convert(Shipping shipping){
        ShippingDTO shippingDTO = new ShippingDTO();
        BeanUtils.copyProperties(shipping, shippingDTO);
        return shippingDTO;
    }
}
