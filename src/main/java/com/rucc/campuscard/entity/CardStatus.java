package com.rucc.campuscard.entity;

/**
 * 校园卡状态枚举
 */
public enum CardStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    DELETED("Deleted");

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
