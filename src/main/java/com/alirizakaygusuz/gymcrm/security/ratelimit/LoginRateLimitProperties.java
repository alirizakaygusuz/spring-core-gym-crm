package com.alirizakaygusuz.gymcrm.security.ratelimit;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.rate-limit.login")
@Getter
@Setter
public class LoginRateLimitProperties {

    @Min(1)
    private int maxAttempts;

    @NotNull
    private Duration blockDuration;

    @NotBlank
    private String redisKeyPrefix;

}
