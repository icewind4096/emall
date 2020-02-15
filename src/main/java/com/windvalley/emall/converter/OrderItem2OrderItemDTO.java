package com.windvalley.emall.converter;

import com.windvalley.emall.dto.OrderItemDTO;
import com.windvalley.emall.pojo.OrderItem;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderItem2OrderItemDTO {
    public static OrderItemDTO convert(OrderItem orderItem){
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        BeanUtils.copyProperties(orderItem, orderItemDTO);
        return orderItemDTO;
    }

    public static List<OrderItemDTO> convert(List<OrderItem> orderItems){
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for (OrderItem orderItem: orderItems){
            OrderItemDTO orderItemDTO = OrderItem2OrderItemDTO.convert(orderItem);
            orderItemDTOs.add(orderItemDTO);
        }
        return orderItemDTOs;
    }
}
