package com.rucc.campuscard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "创建校园卡请求")
public class CreateCardRequest {
    @Schema(description = "学生学号", example = "2023001", required = true)
    private String studentId;
    
    @Schema(description = "学生姓名", example = "张三", required = true)
    private String studentName;

    @Schema(description = "校园卡初始余额，精确到2位小数", example = "100.00", required = false, 
           defaultValue = "0.00")
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
