package com.windvalley.emall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.windvalley.emall.util.serializer.OrderPaymentSatus2StringSerializer;
import com.windvalley.emall.util.serializer.OrderStatus2StringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderNo;

    private Integer shippingId;

    private BigDecimal payment;

    @JsonProperty("paymentTypeDesc")
    @JsonSerialize(using = OrderPaymentSatus2StringSerializer.class)
    private Integer paymentType;

    private Integer postage;

    @JsonProperty("statusDesc")
    @JsonSerialize(using = OrderStatus2StringSerializer.class)
    private Integer status;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date paymentTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date sendTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date closeTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    private String imageHost;

    List<OrderItemDTO> orderItemDTOList;

    private ShippingDTO shippingDTO;
}
