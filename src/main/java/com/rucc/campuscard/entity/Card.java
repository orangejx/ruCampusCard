package com.rucc.campuscard.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Card implements Serializable {
    private UUID id;
    private String studentId;
    private String studentName;
    private BigDecimal balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private CardStatus status;
    private Long createdAt;
    private Long updatedAt;

    public Card() {
        this.id = UUID.randomUUID();
        this.balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.status = CardStatus.ACTIVE;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Card(String studentId) {
        this();
        this.studentId = studentId;
    }

    public Card(String studentId, String studentName) {
        this();
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public Card(String studentId, String studentName, BigDecimal balance) {
        this();
        this.studentId = studentId;
        this.studentName = studentName;
        this.balance = balance != null ? balance.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
        this.balance = balance != null ? balance.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
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

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }
}