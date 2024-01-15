package com.lidachui.lrucache.autoconfigure;

import com.lidachui.lrucache.listener.DefaultCacheListenerProvider;
import com.lidachui.lrucache.util.CacheListenerBeanPostProcessor;
import com.lidachui.lrucache.util.LRUCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * LruCacheAutoConfiguration
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2024/1/12 1:00
 */
@Configuration
@ConditionalOnClass({LRUCache.class})
@Import({DefaultCacheListenerProvider.class})
@EnableConfigurationProperties({LRUCacheProperties.class})
public class LruCacheAutoConfiguration {
    public LruCacheAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean
    public LRUCache lruCache(LRUCacheProperties properties) {
        return new LRUCache<>(properties.getCapacity(), properties.getExpireAfterAccess(), properties.getExpireAfterWrite());
    }

    @Bean
    public CacheListenerBeanPostProcessor cacheListenerBeanPostProcessor() {
        return new CacheListenerBeanPostProcessor();
    }

}





