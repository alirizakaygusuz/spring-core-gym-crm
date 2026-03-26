package com.alirizakaygusuz.gymcrm.security.ratelimit;

import com.alirizakaygusuz.gymcrm.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginRateLimitServiceImpl implements LoginRateLimitService {

    private final RedisTemplate<String, String> redisTemplate;
    private final LoginRateLimitProperties properties;


    public void validateOrThrow(String username) {
        String key = buildKey(username);
        Long attempts = getCurrentAttempts(key);

        if (attempts != null && attempts >= properties.getMaxAttempts()) {
            long remainingTime = getRemainingBlockTime(key);

            log.warn("Login rate limit exceeded for user: {} ({}/{} attempts, blocked for {} seconds)",
                    username, attempts, properties.getMaxAttempts(), remainingTime);

            throw new RateLimitExceededException(username, remainingTime);
        }
    }


    public void recordFailedAttempt(String username) {
        String key = buildKey(username);

        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts == null) {
            attempts = 0L;
        }

        if (attempts == 1) {
            redisTemplate.expire(key, properties.getBlockDuration());
        }

        log.info("Failed login attempt {}/{} for user: {}",
                attempts, properties.getMaxAttempts(), username);

        if (attempts >= properties.getMaxAttempts()) {
            log.warn("User {} blocked for {} after {} failed attempts",
                    username, properties.getBlockDuration(), properties.getMaxAttempts());
        }
    }


    public void resetAttempts(String username) {
        String key = buildKey(username);
        Boolean deleted = redisTemplate.delete(key);

        if (Boolean.TRUE.equals(deleted)) {
            log.debug("Reset login attempts for user: {}", username);
        }
    }


    private Long getCurrentAttempts(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : null;
    }


    private long getRemainingBlockTime(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? ttl : 0;
    }


    private String buildKey(String username) {
        String normalized = username.trim();
        return properties.getRedisKeyPrefix() + normalized;
    }
}