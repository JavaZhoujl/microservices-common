package com.msc.microservices.common.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis分布式锁配置类
 *
 * @author zjl
 */
@Configuration
@ConditionalOnBean(StringRedisTemplate.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisLockAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RedisLock redisLock(StringRedisTemplate stringRedisTemplate) {
        return new RedisLock(stringRedisTemplate);
    }
}
