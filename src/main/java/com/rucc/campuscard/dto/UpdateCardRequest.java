package com.rucc.campuscard.dto;

import com.rucc.campuscard.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "更新校园卡请求")
public class UpdateCardRequest {
    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    @Schema(description = "卡片状态：ACTIVE-正常，LOCKED-锁定，CANCELLED-注销")
    private CardStatus status;

    @Schema(description = "余额变动金额，正数表示充值，负数表示消费，精确到2位小数",
           example = "50.00")
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
