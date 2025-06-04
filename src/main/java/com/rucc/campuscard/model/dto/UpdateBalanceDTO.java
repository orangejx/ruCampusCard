package com.rucc.campuscard.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.RoundingMode;

@Data
public class UpdateBalanceDTO {
    private BigDecimal amount;

    public void setAmount(BigDecimal amount) {
        this.amount = amount != null ? amount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
}
