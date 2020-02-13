package com.windvalley.emall.enums;

import lombok.Getter;

@Getter
public enum CartCheck {
    UNCHECK(0, "未选中"),
    CHECK(1, "选中"),
    ;

    private int code;
    private String descript;

    CartCheck(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }
}
