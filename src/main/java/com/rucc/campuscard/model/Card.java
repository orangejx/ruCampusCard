package com.rucc.campuscard.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Card implements Serializable {

    private String id;
    private String studentId;
    private String studentName;
    private BigDecimal balance;
    private Long createdAt;
    private Long updatedAt;

    // 构造函数
    public Card() {
    }

    public Card(String studentId, String studentName, BigDecimal balance) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.studentName = studentName;
        this.balance = balance != null ? balance.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Card(String id, String studentId, String studentName, BigDecimal balance) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.balance = balance;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
