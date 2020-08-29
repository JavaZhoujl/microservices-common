package com.msc.microservices.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 *
 * @author zjl
 */
public class RedisLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);
    private static final String REDIS_LOCK_SUFFIX = "_redis_lock";
    private final long acquireTimeout;
    /**
     * 最大获取锁超时时间
     */
    private static final long MAX_ACQUIRE_TIME = 3 * 1000;
    private final long lockExpire;
    /**
     * 最大锁过期时间
     */
    private static final long MAX_LOCK_EXPIRE = 30 * 1000;
    /**
     * 默认获取锁重试时间间隔
     */
    private static final long DEFAULT_RETRY_MILLS = 50;
    private StringRedisTemplate stringRedisTemplate;

    public RedisLock(StringRedisTemplate stringRedisTemplate) {
        this(stringRedisTemplate, MAX_ACQUIRE_TIME, MAX_LOCK_EXPIRE);
    }

    public RedisLock(StringRedisTemplate stringRedisTemplate, long acquireTimeout, long lockExpire) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.acquireTimeout = acquireTimeout > 0 ? Math.min(acquireTimeout, MAX_ACQUIRE_TIME) : MAX_ACQUIRE_TIME;
        this.lockExpire = lockExpire > 0 ? Math.min(lockExpire, MAX_LOCK_EXPIRE) : MAX_LOCK_EXPIRE;
    }

    /**
     * 一次性尝试获取锁
     *
     * @param key key
     * @return 是否加锁成功
     */
    public boolean lock(String key) {
        return tryLock(key, 0, this.lockExpire);
    }

    /**
     * 加锁指定Key锁
     *
     * @param key key
     * @return 是否加锁成功
     */
    public boolean tryLock(String key) {
        return tryLock(key, this.acquireTimeout, this.lockExpire);
    }

    /**
     * 加锁指定key锁
     *
     * @param key            key
     * @param acquireTimeout 获取锁超时时间
     * @return 是否加锁成功
     */
    public boolean tryLock(String key, long acquireTimeout) {
        return tryLock(key, acquireTimeout, this.lockExpire);
    }

    /**
     * 加锁指定Key锁
     *
     * @param key            key
     * @param acquireTimeout 获取锁超时时间
     * @param lockExpire     redis锁key过期时间
     * @return 是否加锁成功
     */
    public boolean tryLock(String key, long acquireTimeout, long lockExpire) {
        acquireTimeout = acquireTimeout >= 0 ? Math.min(acquireTimeout, this.acquireTimeout) : this.acquireTimeout;
        lockExpire = lockExpire >= 0 ? Math.min(lockExpire, this.lockExpire) : this.lockExpire;
        long timeout = System.currentTimeMillis() + acquireTimeout;
        String lockKey = key + REDIS_LOCK_SUFFIX;
        String value = key;
        try {
            do {
                if (setNX(lockKey, value)) {
                    stringRedisTemplate.expire(lockKey, lockExpire, TimeUnit.MILLISECONDS);
                    return true;
                } else {
                    LOGGER.debug("Try lock {} for key:{} failed,maybe retry.", lockKey, key);
                }
                Thread.sleep(DEFAULT_RETRY_MILLS);
            } while (timeout - System.currentTimeMillis() >= 0);
        } catch (InterruptedException e) {
            LOGGER.warn("When try lock {} for key:{},thread interrupted", lockKey, key);
        }
        return false;
    }

    /**
     * 释放指定key锁
     *
     * @param key key
     */
    public void unLock(String key) {
        String lockKey = key + REDIS_LOCK_SUFFIX;
        stringRedisTemplate.delete(lockKey);
    }

    public long getAcquireTimeout() {
        return acquireTimeout;
    }

    public long getLockExpire() {
        return lockExpire;
    }

    private boolean setNX(final String key, final String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }
}
