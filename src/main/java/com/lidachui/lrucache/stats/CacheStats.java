package com.lidachui.lrucache.stats;

/**
 * CacheStats @Author lihuijie @Description: @SINCE 2024/1/12 0:38
 */

/**
 * 缓存统计类，用于记录和提供缓存的命中和未命中的统计信息。
 */
public class CacheStats {
    /**
     * 记录缓存命中的次数
     */
    private long hits;
    /**
     * 记录缓存未命中的次数
     */
    private long misses;

    /**
     * 记录一次缓存命中。
     * 当缓存成功返回所请求的元素时调用此方法。
     */
    public synchronized void recordHit() {
        hits++;
    }

    /**
     * 记录一次缓存未命中。
     * 当缓存无法返回所请求的元素（即元素不在缓存中）时调用此方法。
     */
    public synchronized void recordMiss() {
        misses++;
    }

    /**
     * 计算并返回缓存的命中率。
     * 命中率是指命中次数占总请求次数的比例。
     *
     * @return 缓存的命中率，如果没有任何请求则返回1.0。
     */
    public synchronized double hitRate() {
        return hits + misses == 0 ? 1.0 : (double) hits / (hits + misses);
    }

    /**
     * 返回缓存统计的字符串表示形式，包括命中次数、未命中次数和命中率。
     *
     * @return 缓存统计信息的字符串表示。
     */
    @Override
    public synchronized String toString() {
        return String.format("CacheStats[hits=%d, misses=%d, hitRate=%.2f]", hits, misses, hitRate());
    }
}

