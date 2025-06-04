package com.rucc.campuscard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class CreateCardRequest {
    @Schema(description = "学生ID", required = true)
    private String studentId;
    
    @Schema(description = "学生姓名", required = true)
    private String studentName;

    @Schema(description = "校园卡初始余额", required = false)
    private BigDecimal balance;

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
