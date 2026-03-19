package com.skillchain.enums;

public enum OrderStatus {
    PENDING_PAYMENT(0, "待支付"),
    PENDING_ACCEPT(1, "待接单"),
    ACCEPTED(2, "已接单"),
    IN_SERVICE(3, "服务中"),
    PENDING_REVIEW(4, "待评价"),
    COMPLETED(5, "已完成"),
    REFUNDING(6, "退款中"),
    REFUNDED(7, "已退款"),
    CANCELLED(8, "已取消");

    private final Integer code;
    private final String desc;

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderStatus fromCode(Integer code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status code: " + code);
    }
}