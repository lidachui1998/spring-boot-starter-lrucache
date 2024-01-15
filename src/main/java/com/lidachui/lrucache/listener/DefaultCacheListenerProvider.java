package com.lidachui.lrucache.listener;


import org.springframework.stereotype.Component;

import java.util.*;

/**
 * DefaultCacheListenerProvider
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2024/1/12 17:19
 */
@Component
public class DefaultCacheListenerProvider implements CacheListenerProvider {

    private final List<CacheListener> listeners;

    public DefaultCacheListenerProvider(List<CacheListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public List<CacheListener> getCacheListeners() {
        return listeners;
    }
}
