package com.lidachui.lrucache.util;

import com.lidachui.lrucache.listener.CacheListener;
import com.lidachui.lrucache.stats.CacheStats;


import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * LRUCache @Author lihuijie @Description: @SINCE 2024/1/12 0:33 @author lihuijie
 *
 * @param <K> 缓存键的类型
 * @param <V> 缓存值的类型
 * @date 2024/01/12 实现了最近最少使用（LRU）缓存机制的缓存类。 缓存使用一个固定的容量来存储键值对，当缓存达到容量上限时，最老的未使用的条目会被移除。
 */
public class LRUCache<K, V> {
    private final Map<K, V> cache;
    private final Map<K, Long> timestamps;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final int capacity;
    private final long expireAfterAccess; // 单位：毫秒
    private final long expireAfterWrite;  // 单位：毫秒
    private List<CacheListener<K, V>> listeners = new ArrayList<>();
    private final CacheStats cacheStats = new CacheStats();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * 构造一个具有指定容量和过期策略的LRUCache实例。
     *
     * @param capacity          缓存的最大容量
     * @param expireAfterAccess 访问后自动过期时间（单位：秒），小于等于0表示不启用
     * @param expireAfterWrite  写入后自动过期时间（单位：秒），小于等于0表示不启用
     */
    public LRUCache(int capacity, long expireAfterAccess, long expireAfterWrite) {
        this.capacity = capacity;
        this.expireAfterAccess = expireAfterAccess * 1000;
        this.expireAfterWrite = expireAfterWrite * 1000;
        this.cache = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean shouldRemove = size() > LRUCache.this.capacity;
                if (shouldRemove) {
                    timestamps.remove(eldest.getKey()); // 移除时间戳
                    notifyEntryRemoved(eldest.getKey(), eldest.getValue());
                }
                return shouldRemove;
            }
        };
        this.timestamps = new ConcurrentHashMap<>();
        initializeEvictionTask();
    }

    /**
     * 初始化定时清理任务，用于移除过期的缓存条目。 如果设置了过期时间（无论是访问后过期还是写入后过期），则会启动一个定时任务， 每秒检查并清理过期的缓存条目。
     */
    private void initializeEvictionTask() {
        if (expireAfterAccess > 0 || expireAfterWrite > 0) {
            executor.scheduleWithFixedDelay(this::evictExpiredEntries, 1, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * 清理过期的缓存条目。此方法会遍历缓存，移除那些已经过期的条目。 如果设置了监听器，会在移除条目时调用监听器的onEntryRemoved方法。
     */
    private void evictExpiredEntries() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<K, Long>> iterator = timestamps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Long> entry = iterator.next();
            K key = entry.getKey();
            long time = entry.getValue();
            if ((expireAfterAccess > 0 && now - time > expireAfterAccess) ||
                    (expireAfterWrite > 0 && now - time > expireAfterWrite)) {
                iterator.remove();
                V value = cache.remove(key);
                notifyEntryRemoved(key, value);
            }
        }
    }

    /**
     * 根据键获取缓存中的值。如果键存在，则更新其访问时间戳。
     *
     * @param key 缓存键
     * @return 缓存中的值，如果不存在则返回null
     */
    public V get(K key) {
        lock.readLock().lock();
        try {
            V value = cache.get(key);
            if (value != null) {
                timestamps.put(key, System.currentTimeMillis());
                cacheStats.recordHit();
            } else {
                cacheStats.recordMiss();
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 根据键手动删除缓存中的条目。
     *
     * @param key 要删除的缓存条目的键
     */
    public void remove(K key) {
        lock.writeLock().lock();
        try {
            if (cache.containsKey(key)) {
                V value = cache.remove(key);
                timestamps.remove(key);
                notifyEntryRemoved(key, value);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 将键值对放入缓存，并设置一个特定的过期时间。
     *
     * @param key           缓存键
     * @param value         缓存值
     * @param expireAfterMs 过期时间（单位：毫秒）
     */
    public void putWithExpire(K key, V value, long expireAfterMs) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Cache keys and values cannot be null.");
        }
        lock.writeLock().lock();
        try {
            cache.put(key, value);
            long expiryTime = System.currentTimeMillis() + expireAfterMs;
            timestamps.put(key, expiryTime);
            notifyEntryAdded(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 将键值对放入缓存。如果键已存在，则更新其值和写入时间戳。
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Cache keys and values cannot be null.");
        }
        lock.writeLock().lock();
        try {
            cache.put(key, value);
            timestamps.put(key, System.currentTimeMillis());
            notifyEntryAdded(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 获取缓存统计信息
     *
     * @return {@code CacheStats}
     */
    public CacheStats getCacheStats() {
        return cacheStats;
    }

    /**
     * 获取缓存的当前大小。
     *
     * @return 缓存中的条目数量
     */
    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 清空缓存。
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
            timestamps.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 获取缓存中的所有键值对。
     *
     * @return 包含所有缓存条目的Map
     */
    public Map<K, V> getAll() {
        lock.readLock().lock();
        try {
            return new LinkedHashMap<>(cache);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取访问频率靠前的n个缓存条目
     *
     * @param n n
     * @return {@code List<Map.Entry<K, V>>}
     */
    public List<Map.Entry<K, V>> getEntriesByFrequency(int n) {
        lock.readLock().lock();
        try {
            return new ArrayList<>(cache.entrySet()).stream()
                    .sorted((e1, e2) -> -Long.compare(timestamps.get(e1.getKey()), timestamps.get(e2.getKey())))
                    .limit(n)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * 通知监听器删除事件
     *
     * @param key   键
     * @param value 值
     */
    private void notifyEntryRemoved(K key, V value) {
        if (listeners != null) {
            for (CacheListener<K, V> listener : listeners) {
                listener.onEntryRemoved(key, value);
            }
        }
    }

    /**
     * 通知监听器添加事件
     *
     * @param key   键
     * @param value 值
     */
    private void notifyEntryAdded(K key, V value) {
        if (listeners != null) {
            for (CacheListener<K, V> listener : listeners) {
                listener.onEntryAdded(key, value);
            }
        }
    }

    void addCacheListener(CacheListener<K, V> listener) {
        this.listeners.add(listener);
    }

    /**
     * 关闭缓存，释放资源。 应在缓存不再使用时调用此方法。
     */
    public void shutdown() {
        executor.shutdownNow();
    }

    /**
     * 确保资源被释放。 在对象被垃圾回收时调用。
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        shutdown();
    }
}
