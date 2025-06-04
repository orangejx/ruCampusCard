package com.rucc.campuscard.service;

import com.rucc.campuscard.entity.Card;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.UUID;

public interface CardService {
    /**
     * 创建新的校园卡
     * @param studentId 学生ID
     * @param studentName 学生姓名
     * @param balance 初始余额
     * @return 创建的校园卡
     * @throws IllegalStateException 如果该学生已经有校园卡
     */
    Card createCard(String studentId, String studentName, BigDecimal balance);

    /**
     * 通过卡片ID查询校园卡
     * @param id 卡片ID
     * @return 校园卡信息
     * @throws IllegalArgumentException 如果卡片不存在
     */
    Card getCard(UUID id);

    /**
     * 通过学生ID查询校园卡
     * @param studentId 学生ID
     * @return 校园卡信息
     * @throws IllegalArgumentException 如果卡片不存在
     */
    Card getCardByStudentId(String studentId);

    /**
     * 更新校园卡余额
     * @param id 卡片ID
     * @param amount 金额变化（可以是正数或负数）
     * @return 更新后的校园卡信息
     * @throws IllegalArgumentException 如果卡片不存在或余额不足
     */
    Card updateBalance(UUID id, BigDecimal amount);

    /**
     * 更新学生姓名
     * @param id 卡片ID
     * @param studentName 新的学生姓名
     * @return 更新后的校园卡信息
     * @throws IllegalArgumentException 如果卡片不存在
     */
    Card updateStudentName(UUID id, String studentName);

    /**
     * 通过卡片ID删除校园卡
     * @param id 卡片ID
     * @throws IllegalArgumentException 如果卡片不存在
     */
    void deleteCard(UUID id);

    /**
     * 通过学生ID删除校园卡
     * @param studentId 学生ID
     * @throws IllegalArgumentException 如果卡片不存在
     */
    void deleteCardByStudentId(String studentId);

    /**
     * 通过学生ID更新学生姓名
     * @param studentId 学生ID
     * @param studentName 新的学生姓名
     * @return 更新后的校园卡信息
     * @throws IllegalArgumentException 如果卡片不存在
     */
    Card updateStudentNameByStudentId(String studentId, String studentName);

    /**
     * 通过学生ID更新校园卡余额
     * @param studentId 学生ID
     * @param amount 金额变化（可以是正数或负数）
     * @return 更新后的校园卡信息
     * @throws IllegalArgumentException 如果卡片不存在或余额不足
     */
    Card updateBalanceByStudentId(String studentId, BigDecimal amount);

    /**
     * 获取所有校园卡的分页列表
     * @param page 页码
     * @param size 每页大小
     * @param studentId 学生ID筛选条件(可选)
     * @param studentName 学生姓名筛选条件(可选)
     * @return 分页的校园卡列表
     */
    Page<Card> getAllCards(Integer page, Integer size, String studentId, String studentName);
}