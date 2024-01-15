package com.lidachui.lrucache.listener;

/**
 * CacheListener @Author lihuijie @Description: @SINCE 2024/1/12 0:37 缓存侦听器 缓存监听器接口，用于监听缓存中条目的添加和移除事件。
 * 实现此接口的类可以接收到缓存中条目被添加或移除时的通知。
 *
 * @param <K> 缓存键的类型
 * @param <V> 缓存值的类型
 * @date 2024/01/12
 */
public interface CacheListener<K, V> {

    /**
     * 当缓存中的一个条目被移除时调用此方法。
     *
     * @param key   被移除条目的键
     * @param value 被移除条目的值
     */
    void onEntryRemoved(K key, V value);

    /**
     * 当缓存中添加了一个新的条目时调用此方法。
     *
     * @param key   新添加条目的键
     * @param value 新添加条目的值
     */
    void onEntryAdded(K key, V value);
}
