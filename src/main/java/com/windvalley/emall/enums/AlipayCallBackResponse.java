package com.windvalley.emall.enums;

import lombok.Getter;

@Getter
public enum AlipayCallBackResponse {
    SUCCESS(0, "success"),
    ERROR(1, "error"),
    ;

    private int code;
    private String descript;

    AlipayCallBackResponse(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }
}
