package com.windvalley.emall.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ProductStatus {
    ONLINE(1, "在售"),
    OFFLINE(2, "下架"),
    DELETE(3, "删除"),
    ;

    private int code;
    private String descript;

    ProductStatus(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }
}
