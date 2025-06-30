package com.university.bookstore.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 缓存管理器
 * 提供简单的内存缓存功能，减少数据库查询
 */
public class CacheManager {
    private static final ConcurrentHashMap<String, CacheItem> cache = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // 默认缓存时间（毫秒）
    private static final long DEFAULT_TTL = 30000; // 30秒
    
    static {
        // 启动定期清理过期缓存的任务
        scheduler.scheduleAtFixedRate(CacheManager::cleanExpiredItems, 60, 60, TimeUnit.SECONDS);
    }
    
    /**
     * 缓存项
     */
    private static class CacheItem {
        private final Object value;
        private final long expireTime;
        
        public CacheItem(Object value, long ttl) {
            this.value = value;
            this.expireTime = System.currentTimeMillis() + ttl;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
        
        public Object getValue() {
            return value;
        }
    }
    
    /**
     * 存储缓存项
     * @param key 缓存键
     * @param value 缓存值
     */
    public static void put(String key, Object value) {
        put(key, value, DEFAULT_TTL);
    }
    
    /**
     * 存储缓存项
     * @param key 缓存键
     * @param value 缓存值
     * @param ttl 生存时间（毫秒）
     */
    public static void put(String key, Object value, long ttl) {
        cache.put(key, new CacheItem(value, ttl));
    }
    
    /**
     * 获取缓存项
     * @param key 缓存键
     * @return 缓存值，如果不存在或已过期则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        CacheItem item = cache.get(key);
        if (item == null || item.isExpired()) {
            cache.remove(key);
            return null;
        }
        return (T) item.getValue();
    }
    
    /**
     * 移除缓存项
     * @param key 缓存键
     */
    public static void remove(String key) {
        cache.remove(key);
    }
    
    /**
     * 清空所有缓存
     */
    public static void clear() {
        cache.clear();
    }
    
    /**
     * 根据键前缀清除缓存
     * @param pattern 键前缀模式
     */
    public static void clearByPattern(String pattern) {
        cache.entrySet().removeIf(entry -> entry.getKey().startsWith(pattern));
    }
    
    /**
     * 清理过期的缓存项
     */
    private static void cleanExpiredItems() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * 获取缓存大小
     * @return 缓存项数量
     */
    public static int size() {
        return cache.size();
    }
    
    /**
     * 关闭缓存管理器
     */
    public static void shutdown() {
        scheduler.shutdown();
        cache.clear();
    }
    
    /**
     * 生成缓存键
     * @param prefix 前缀
     * @param params 参数
     * @return 缓存键
     */
    public static String generateKey(String prefix, Object... params) {
        StringBuilder sb = new StringBuilder(prefix);
        for (Object param : params) {
            sb.append(":").append(param);
        }
        return sb.toString();
    }
}