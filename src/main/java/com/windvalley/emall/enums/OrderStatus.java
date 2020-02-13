package com.windvalley.emall.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CANCELED(10, "已取消"),
    NOPAY(20, "未支付"),
    PAIED(30, "已支付"),
    SHIPPED(40, "已发货"),
    SUCCESS(50, "订单完成"),
    CLOSED(60, "订单关闭"),
    ;

    private int code;
    private String descript;

    OrderStatus(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }
}
