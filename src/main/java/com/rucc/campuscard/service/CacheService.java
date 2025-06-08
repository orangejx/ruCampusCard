package com.rucc.campuscard.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class CacheService {
    private static final String CARD_SET_KEY = "cards:all";
    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取Hash中的所有字段和值
     */
    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取Hash中指定字段的值
     */
    public String hget(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    /**
     * 清理字符串值，移除额外的引号
     */
    private String cleanValue(Object value) {
        if (value == null) return "";
        String str = value.toString();
        // 如果字符串被引号包围，移除它们
        if (str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * 设置Hash中的多个字段和值
     */
    public void hmset(String key, Map<String, Object> hash) {
        Map<String, Object> cleanHash = hash.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                e -> cleanValue(e.getValue())
            ));
        redisTemplate.opsForHash().putAll(key, cleanHash);
    }

    /**
     * 设置Hash中的单个字段和值
     */
    public void hset(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, cleanValue(value));
    }

    /**
     * 删除Hash中的字段
     */
    public void hdel(String key, String... fields) {
        redisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    /**
     * 将卡片ID添加到Set中
     */
    public void addCardToSet(String cardId) {
        redisTemplate.opsForSet().add(CARD_SET_KEY, cleanValue(cardId));
    }

    /**
     * 从Set中移除卡片ID
     */
    public void removeCardFromSet(String cardId) {
        redisTemplate.opsForSet().remove(CARD_SET_KEY, cardId);
    }

    /**
     * 获取所有卡片ID
     */
    public Set<Object> getAllCardIds() {
        return redisTemplate.opsForSet().members(CARD_SET_KEY);
    }

    /**
     * 生成卡片的Redis key
     * 注意：需要使用大括号来确保在集群模式下key被正确路由
     */
    public static String getCardKey(String cardId) {
        return String.format("card:{%s}", cardId);
    }
}
