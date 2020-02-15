package com.windvalley.emall.converter;

import com.windvalley.emall.dto.OrderDTO;
import com.windvalley.emall.pojo.Order;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class Order2OrderDTO {
    public static OrderDTO convert(Order order){
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        return orderDTO;
    }

    public static List convert(List<Order> orders) {
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order: orders){
            OrderDTO orderDTO = convert(order);
            orderDTOs.add(orderDTO);
        }
        return orderDTOs;
    }
}
