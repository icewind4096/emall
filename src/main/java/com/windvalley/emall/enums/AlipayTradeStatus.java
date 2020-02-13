package com.windvalley.emall.enums;

import lombok.Getter;

@Getter
public enum AlipayTradeStatus {
    WAIT(10, "WAIT_BUYER_PAY"),                 //交易创建，等待买家付款
    CLOSE(20, "TRADE_CLOSED"),                  //未付款交易超时关闭，或支付完成后全额退款
    SUCCESS(30, "TRADE_SUCCESS"),               //交易支付成功
    FINISH(40, "TRADE_FINISHED"),               //交易结束，不可退款
    ;

    private int code;
    private String descript;

    AlipayTradeStatus(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }

}
