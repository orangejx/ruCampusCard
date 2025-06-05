package com.rucc.campuscard.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "校园卡状态枚举")
public enum CardStatus {
    @Schema(description = "正常状态")
    ACTIVE("正常"),
    
    @Schema(description = "锁定状态")
    INACTIVE("锁定"),
    
    @Schema(description = "已注销")
    DELETED("已注销");

    private final String value;

    CardStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CardStatus fromValue(String value) {
        for (CardStatus status : CardStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid CardStatus value: " + value);
    }
}
