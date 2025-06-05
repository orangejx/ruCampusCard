package com.rucc.campuscard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CARDS_SET_KEY = "cards:all"; // 存储所有卡片ID的集合key

    public CacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper redisObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = redisObjectMapper;
    }

    /**
     * 将对象的所有字段存储到hash中
     * @param key hash的key（例如：card:${cardId}）
     * @param object 要存储的对象
     */
    public void hmset(String key, Object object) {
        Map<String, Object> fields = objectMapper.convertValue(object, Map.class);
        redisTemplate.opsForHash().putAll(key, fields);
    }

    /**
     * 将多个字段值存储到hash中
     * @param key hash的key
     * @param fields 字段值映射
     */
    public void hmset(String key, Map<String, Object> fields) {
        redisTemplate.opsForHash().putAll(key, fields);
    }

    /**
     * 将值存储到hash中
     * @param key hash的key
     * @param field hash的field
     * @param value 要存储的值
     */
    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 从hash中获取值
     * @param key hash的key
     * @param field hash的field
     * @return 存储的值
     */
    public String hget(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    /**
     * 从hash中获取多个字段的值
     * @param key hash的key
     * @param fields 要获取的字段列表
     * @return 字段值列表
     */
    public List<Object> hmget(String key, Collection<String> fields) {
        // 将Collection<String>转换为Collection<Object>
        Collection<Object> objectFields = new ArrayList<>(fields);
        return redisTemplate.opsForHash().multiGet(key, objectFields);
    }

    /**
     * 从hash中获取所有字段和值
     * @param key hash的key
     * @return 所有字段和值的映射
     */
    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 从hash中删除一个或多个field
     * @param key hash的key
     * @param fields 要删除的field
     * @return 删除的field数量
     */
    public Long hdel(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 检查hash中是否存在field
     * @param key hash的key
     * @param field 要检查的field
     * @return 是否存在
     */
    public boolean hexists(String key, String field) {
        return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, field));
    }

    /**
     * 获取hash中的所有字段名
     * @param key hash的key
     * @return 字段名集合
     */
    public Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取hash中的所有值
     * @param key hash的key
     * @return 值集合
     */
    public List<Object> hvals(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 将ID添加到卡片集合中
     * @param cardId 卡片ID
     * @return 是否添加成功
     */
    public boolean addCardToSet(String cardId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().add(CARDS_SET_KEY, cardId));
    }

    /**
     * 从卡片集合中移除ID
     * @param cardId 卡片ID
     * @return 是否移除成功
     */
    public boolean removeCardFromSet(String cardId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().remove(CARDS_SET_KEY, cardId));
    }

    /**
     * 获取所有卡片ID
     * @return 卡片ID集合
     */
    public Set<Object> getAllCardIds() {
        return redisTemplate.opsForSet().members(CARDS_SET_KEY);
    }

    /**
     * 检查卡片ID是否存在于集合中
     * @param cardId 卡片ID
     * @return 是否存在
     */
    public boolean isCardInSet(String cardId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(CARDS_SET_KEY, cardId));
    }

    /**
     * 存储数据到Redis
     * @param key 键
     * @param value 值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 从Redis获取数据
     * @param key 键
     * @return 存储的值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 从Redis获取数据并转换为指定类型
     * @param key 键
     * @param clazz 目标类型
     * @param <T> 类型参数
     * @return 转换后的值
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof LinkedHashMap) {
            return objectMapper.convertValue(value, clazz);
        }
        return clazz.cast(value);
    }

    /**
     * 删除Redis中的数据
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 检查键是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 获取hash中的所有键值对并转换为指定类型
     * @param key hash的key
     * @param clazz 目标类型
     * @param <T> 类型参数
     * @return 包含所有键值对的Map
     */
    public <T> Map<String, T> hgetAll(String key, Class<T> clazz) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Map<String, T> result = new LinkedHashMap<>();
        
        entries.forEach((field, value) -> {
            if (value instanceof LinkedHashMap) {
                result.put(field.toString(), objectMapper.convertValue(value, clazz));
            } else {
                result.put(field.toString(), clazz.cast(value));
            }
        });
        
        return result;
    }
}
