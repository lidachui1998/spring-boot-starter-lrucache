package com.lidachui.lrucache.util;

import com.lidachui.lrucache.listener.CacheListener;
import javax.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * CacheListenerBeanPostProcessor
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2024/1/12 23:11
 */
public class CacheListenerBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private LRUCache<String, Object> lruCache;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof CacheListener) {
            lruCache.addCacheListener((CacheListener<String, Object>) bean);
        }
        return bean;
    }
}

