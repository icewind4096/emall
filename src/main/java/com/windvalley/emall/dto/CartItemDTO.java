package com.windvalley.emall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartItemDTO {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer check;

    private Integer quantity;

    private String productName;

    private String productSubtitle;

    private String productMainImage;

    private BigDecimal productPrice;

    private Integer productStock;

    private Integer productStatus;

    private BigDecimal productAmount;
}
