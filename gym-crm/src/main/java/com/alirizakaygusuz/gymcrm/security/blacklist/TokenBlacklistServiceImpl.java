package com.alirizakaygusuz.gymcrm.security.blacklist;

import com.alirizakaygusuz.gymcrm.security.authentication.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;
    private final TokenBlacklistProperties properties;


    public void blacklist(String token) {
        if (!properties.isEnabled()) {
            log.debug("Token blacklist is disabled");
            return;
        }

        if (token == null || token.isBlank()) {
            log.warn("Attempted to blacklist null/empty token");
            return;
        }

        if (!jwtService.isTokenValid(token)) {
            log.debug("Token already invalid/expired, skipping blacklist");
            return;
        }

        long remainingTimeMs = jwtService.getRemainingExpirationTime(token);

        if (remainingTimeMs <= 0) {
            log.debug("Token already expired, skipping blacklist");
            return;
        }

        String key = buildKey(token);
        redisTemplate.opsForValue().set(key, "BLACKLISTED", remainingTimeMs, TimeUnit.MILLISECONDS);

        log.info("Token blacklisted for {} seconds", remainingTimeMs / 1000);
    }


    public boolean isBlacklisted(String token) {
        if (!properties.isEnabled()) {
            return false;
        }

        if (token == null || token.isBlank()) {
            return false;
        }

        String key = buildKey(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }


    private String buildKey(String token) {
        return properties.getRedisKeyPrefix() + token;
    }
}