package com.rucc.campuscard.service.impl;

import com.rucc.campuscard.entity.Card;
import com.rucc.campuscard.entity.CardStatus;
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

    private static final String STUDENT_CARDS_KEY = "student_cards";

    private final CacheService cacheService;

    public CardServiceImpl(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * 获取卡片的Redis键
     * @param cardId 卡片ID
     * @return Redis键
     */
    private String getCardKey(UUID cardId) {
        // 使用CacheService中的方法来确保键格式正确
        return CacheService.getCardKey(cardId.toString());
    }

    /**
     * 从Redis Hash中构建Card对象
     * @param cardKey 卡片键
     * @return Card对象
     */
    private Card buildCardFromHash(String cardKey) {
        Map<Object, Object> cardData = cacheService.hgetAll(cardKey);
        if (cardData == null || cardData.isEmpty()) {
            return null;
        }

        Card card = new Card();
        card.setId(UUID.fromString(cardData.get("id").toString()));
        card.setStudentId(cardData.get("studentId").toString());
        card.setStudentName(cardData.get("studentName").toString());
        card.setBalance(new BigDecimal(cardData.get("balance").toString()));
        card.setStatus(CardStatus.valueOf(cardData.get("status").toString()));
        card.setCreatedAt(Long.parseLong(cardData.get("createdAt").toString()));
        card.setUpdatedAt(Long.parseLong(cardData.get("updatedAt").toString()));
        
        return card;
    }

    /**
     * 将Card对象转换为Hash字段映射
     * 确保所有值都是原始字符串，不包含额外的引号
     * @param card Card对象
     * @return Hash字段映射
     */
    private Map<String, Object> cardToHashMap(Card card) {
        Map<String, Object> hash = new HashMap<>();
        // 直接使用toString()方法，不添加额外的引号
        hash.put("id", card.getId().toString());
        hash.put("studentId", card.getStudentId());
        hash.put("studentName", card.getStudentName());
        hash.put("balance", card.getBalance().setScale(2, RoundingMode.HALF_UP).toString());
        hash.put("status", card.getStatus().toString());
        hash.put("createdAt", String.valueOf(card.getCreatedAt()));
        hash.put("updatedAt", String.valueOf(card.getUpdatedAt()));
        return hash;
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
        
        // 保存卡片信息到Hash表，使用纯字符串值
        String cardKey = getCardKey(card.getId());
        Map<String, Object> cardData = cardToHashMap(card);
        cacheService.hmset(cardKey, cardData);
        
        // 保存学生ID到卡片ID的映射，确保ID是纯字符串
        String cardId = card.getId().toString();
        cacheService.hset(STUDENT_CARDS_KEY, studentId, cardId);
        
        // 添加卡片ID到集合，确保ID是纯字符串
        cacheService.addCardToSet(cardId);
        
        return card;
    }

    @Override
    public Card getCard(UUID id) {
        String cardKey = getCardKey(id);
        Card card = buildCardFromHash(cardKey);
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
        String cardKey = getCardKey(id);
        Card card = buildCardFromHash(cardKey);
        if (card == null) {
            throw new IllegalArgumentException("Card not found: " + id);
        }

        BigDecimal newBalance = card.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // 更新余额和更新时间
        long updatedAt = System.currentTimeMillis();
        Map<String, Object> updates = new HashMap<>();
        updates.put("balance", newBalance.toString());
        updates.put("updatedAt", String.valueOf(updatedAt));
        cacheService.hmset(cardKey, updates);

        // 更新内存中的对象
        card.setBalance(newBalance);
        card.setUpdatedAt(updatedAt);
        
        return card;
    }

    @Override
    public Card updateStudentName(UUID id, String studentName) {
        String cardKey = getCardKey(id);
        Card card = buildCardFromHash(cardKey);
        if (card == null) {
            throw new IllegalArgumentException("Card not found: " + id);
        }

        // 更新学生姓名和更新时间
        long updatedAt = System.currentTimeMillis();
        Map<String, Object> updates = new HashMap<>();
        updates.put("studentName", studentName);
        updates.put("updatedAt", String.valueOf(updatedAt));
        cacheService.hmset(cardKey, updates);

        // 更新内存中的对象
        card.setStudentName(studentName);
        card.setUpdatedAt(updatedAt);
        
        return card;
    }

    @Override
    public void deleteCard(UUID id) {
        String cardKey = getCardKey(id);
        Card card = buildCardFromHash(cardKey);
        if (card == null) {
            throw new IllegalArgumentException("Card not found: " + id);
        }
        
        // 删除卡片信息
        cacheService.hdel(cardKey, "id", "studentId", "studentName", "balance", "status", "createdAt", "updatedAt");
        
        // 删除学生ID到卡片ID的映射
        cacheService.hdel(STUDENT_CARDS_KEY, card.getStudentId());
        
        // 从卡片集合中移除
        cacheService.removeCardFromSet(id.toString());
    }

    @Override
    public void deleteCardByStudentId(String studentId) {
        // 获取卡片ID
        String cardId = cacheService.hget(STUDENT_CARDS_KEY, studentId);
        if (cardId == null) {
            throw new IllegalArgumentException("No card found for student: " + studentId);
        }
        
        // 删除卡片
        deleteCard(UUID.fromString(cardId));
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
        // 从集合中获取所有卡片ID
        Set<Object> cardIds = cacheService.getAllCardIds();
        
        // 获取所有卡片并应用筛选条件
        List<Card> filteredCards = cardIds.stream()
                .map(id -> {
                    try {
                        // 清理cardId，移除可能存在的引号
                        String cardId = id.toString().replaceAll("^\"|\"$", "");
                        return getCard(UUID.fromString(cardId));
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
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