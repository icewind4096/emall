package com.windvalley.emall.enums;

import lombok.Getter;

@Getter
public enum PayPlatform {
    Alipay(1, "支付宝"),
    Weichat(2, "微信"),
            ;

    private int code;
    private String descript;

    PayPlatform(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }

}
