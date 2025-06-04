package com.rucc.campuscard.service.impl;

import com.rucc.campuscard.entity.Card;
import com.rucc.campuscard.service.CardService;
import com.rucc.campuscard.service.CacheService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Service
public class CardServiceImpl implements CardService {

    private static final String CARDS_KEY = "cards";
    private static final String STUDENT_CARDS_KEY = "student_cards";

    private final CacheService cacheService;

    public CardServiceImpl(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public Card createCard(String studentId, String studentName, BigDecimal balance) {
        // 检查学生是否已有校园卡
        String existingCardId = cacheService.hget(STUDENT_CARDS_KEY, studentId);
        if (existingCardId != null) {
            throw new IllegalStateException("Student already has a card: " + studentId);
        }

        // 创建新卡
        BigDecimal initialBalance = (balance != null && balance.compareTo(BigDecimal.ZERO) >= 0) ? 
            balance.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        Card card = new Card(studentId, studentName, initialBalance);
        
        // 保存卡片信息
        cacheService.hset(CARDS_KEY, card.getId().toString(), card);
        
        // 保存学生ID到卡片ID的映射
        cacheService.hset(STUDENT_CARDS_KEY, studentId, card.getId().toString());
        
        return card;
    }

    @Override
    public Card getCard(UUID id) {
        Card card = cacheService.hget(CARDS_KEY, id.toString(), Card.class);
        if (card == null) {
            throw new IllegalArgumentException("Card not found: " + id);
        }
        return card;
    }

    @Override
    public Card getCardByStudentId(String studentId) {
        String cardId = cacheService.hget(STUDENT_CARDS_KEY, studentId);
        if (cardId == null) {
            throw new IllegalArgumentException("No card found for student: " + studentId);
        }
        return getCard(UUID.fromString(cardId));
    }

    @Override
    public Card updateBalance(UUID id, BigDecimal amount) {
        Card card = getCard(id);
        BigDecimal newBalance = card.getBalance().add(amount);
        
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        
        card.setBalance(newBalance);
        // 更新时间
        card.setUpdatedAt(System.currentTimeMillis());
        
        // 更新卡片信息
        cacheService.hset(CARDS_KEY, id.toString(), card);
        
        return card;
    }

    @Override
    public Card updateStudentName(UUID id, String studentName) {
        Card card = getCard(id);
        card.setStudentName(studentName);
        card.setUpdatedAt(System.currentTimeMillis());
        
        // 更新卡片信息
        cacheService.hset(CARDS_KEY, id.toString(), card);
        
        return card;
    }

    @Override
    public void deleteCard(UUID id) {
        // 先获取卡片信息，确认卡片存在
        Card card = getCard(id);
        
        // 删除学生ID到卡片ID的映射
        cacheService.hdel(STUDENT_CARDS_KEY, card.getStudentId());
        
        // 删除卡片信息
        cacheService.hdel(CARDS_KEY, id.toString());
    }

    @Override
    public void deleteCardByStudentId(String studentId) {
        // 获取卡片ID
        String cardId = cacheService.hget(STUDENT_CARDS_KEY, studentId);
        if (cardId == null) {
            throw new IllegalArgumentException("No card found for student: " + studentId);
        }
        
        // 删除卡片信息
        cacheService.hdel(CARDS_KEY, cardId);
        
        // 删除学生ID到卡片ID的映射
        cacheService.hdel(STUDENT_CARDS_KEY, studentId);
    }

    @Override
    public Card updateStudentNameByStudentId(String studentId, String studentName) {
        // 获取卡片ID
        String cardId = cacheService.hget(STUDENT_CARDS_KEY, studentId);
        if (cardId == null) {
            throw new NoSuchElementException("No card found for student: " + studentId);
        }
        
        // 获取卡片并更新学生姓名
        UUID id = UUID.fromString(cardId);
        return updateStudentName(id, studentName);
    }

    @Override
    public Card updateBalanceByStudentId(String studentId, BigDecimal amount) {
        // 获取卡片ID
        String cardId = cacheService.hget(STUDENT_CARDS_KEY, studentId);
        if (cardId == null) {
            throw new NoSuchElementException("No card found for student: " + studentId);
        }
        
        // 获取卡片并更新余额
        UUID id = UUID.fromString(cardId);
        return updateBalance(id, amount);
    }

    @Override
    public Page<Card> getAllCards(Integer page, Integer size, String studentId, String studentName) {
        // 获取所有卡片
        Map<String, Card> allCards = cacheService.hgetAll(CARDS_KEY, Card.class);
        List<Card> cardList = new ArrayList<>(allCards.values());

        // 应用筛选条件
        List<Card> filteredCards = cardList.stream()
                .filter(card -> studentId == null || studentId.isEmpty() || card.getStudentId().contains(studentId))
                .filter(card -> studentName == null || studentName.isEmpty() || card.getStudentName().contains(studentName))
                .sorted(Comparator.comparing(Card::getCreatedAt).reversed())
                .collect(Collectors.toList());

        // 计算分页参数
        int totalElements = filteredCards.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        // 返回分页结果
        return new PageImpl<>(
                filteredCards.subList(fromIndex, toIndex),
                PageRequest.of(page, size),
                totalElements
        );
    }
}