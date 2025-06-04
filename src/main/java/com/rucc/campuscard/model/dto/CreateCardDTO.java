package com.rucc.campuscard.model.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

@Data
public class CreateCardDTO {
    private String studentId;
    private String studentName;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "余额不能小于0")
    @Digits(integer = 10, fraction = 2, message = "余额最多支持小数点后两位")
    private BigDecimal balance = BigDecimal.ZERO; // 默认余额为0.0

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
