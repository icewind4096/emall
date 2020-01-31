package com.windvalley.emall.enums;

import lombok.Getter;

@Getter
public enum RoleCode {
    CUSTOMER(0, "普通用户"),
    ADMIN(1, "管理员"),
    ;

    private int code;
    private String descript;

    RoleCode(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }
}
