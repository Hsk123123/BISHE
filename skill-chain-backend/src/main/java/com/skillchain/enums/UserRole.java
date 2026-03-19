package com.skillchain.enums;

public enum UserRole {
    USER(0, "普通用户"),
    WORKER(1, "工作者"),
    ADMIN(2, "管理员");

    private final Integer code;
    private final String desc;

    UserRole(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}