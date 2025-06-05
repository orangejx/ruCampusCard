package com.rucc.campuscard.entity;

public enum CardStatus {
    ACTIVE("正常"),
    INACTIVE("锁定"),
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
