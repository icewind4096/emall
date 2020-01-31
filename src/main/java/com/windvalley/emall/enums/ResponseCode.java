package com.windvalley.emall.enums;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NEED_LOGIN(2, "NEED LOGIN"),
    ILLEGAL_ARGUMENT(3, "ILLEGAL_ARGUMENT"),
    ;

    private int code;
    private String descript;

    ResponseCode(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }
}
