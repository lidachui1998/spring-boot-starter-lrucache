package com.lidachui.lrucache.listener;

import java.util.*;

/**
 * 缓存侦听器提供程序
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2024/1/12 17:04
 * @date 2024/01/12
 */
public interface CacheListenerProvider<K, V> {
    /**
     * 获取缓存侦听器
     *
     * @return {@code List<CacheListener<K, V>>}
     */
    List<CacheListener<K, V>> getCacheListeners();
}

