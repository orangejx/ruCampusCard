package com.rucc.campuscard.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Schema(description = "校园卡实体类，包含学生信息和卡片状态")
public class Card implements Serializable {
    @Schema(description = "卡片唯一标识符", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "学生学号", example = "2023001")
    private String studentId;

    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    @Schema(description = "卡片余额，精确到2位小数", example = "100.00")
    private BigDecimal balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Schema(description = "卡片状态：ACTIVE-正常，LOCKED-锁定，CANCELLED-注销")
    private CardStatus status;

    @Schema(description = "创建时间戳（毫秒）", example = "1677649420000")
    private Long createdAt;

    @Schema(description = "最后更新时间戳（毫秒）", example = "1677649420000")
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