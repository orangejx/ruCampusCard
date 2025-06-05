package com.rucc.campuscard.dto;

import com.rucc.campuscard.entity.CardStatus;
import java.math.BigDecimal;

public class UpdateCardRequest {
    private String studentName;
    private CardStatus status;
    private BigDecimal balanceChange;

    // Getters and Setters
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public BigDecimal getBalanceChange() {
        return balanceChange;
    }

    public void setBalanceChange(BigDecimal balanceChange) {
        this.balanceChange = balanceChange;
    }
}
