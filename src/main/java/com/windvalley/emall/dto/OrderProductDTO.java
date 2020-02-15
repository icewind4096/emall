package com.windvalley.emall.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderProductDTO {
    private BigDecimal payment;

    private String imageHost;

    List<OrderItemDTO> orderItemDTOList;
}
