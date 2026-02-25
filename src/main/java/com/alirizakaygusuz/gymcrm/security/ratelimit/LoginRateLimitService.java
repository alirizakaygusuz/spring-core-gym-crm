package com.alirizakaygusuz.gymcrm.security.ratelimit;

public interface LoginRateLimitService {

    void validateOrThrow(String username);

    void recordFailedAttempt(String username);

    void resetAttempts(String username);
}
