package com.lidachui.lrucache.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * LruCacheProperties
 *
 * @Author lihuijie
 * @Description:
 * @SINCE 2024/1/12 1:02
 */
@ConfigurationProperties(prefix = "lru.cache")
public class LRUCacheProperties {
    /**
     * 容量
     */
    private int capacity = 100; // 默认容量

    /**
     * 访问后过期时间
     */
    private long expireAfterAccess = -1; // 访问后过期时间，单位秒，默认不过期
    /**
     * 写入后过期时间
     */
    private long expireAfterWrite = -1; // 写入后过期时间，单位秒，默认不过期

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(long expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
    }

    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }
}
