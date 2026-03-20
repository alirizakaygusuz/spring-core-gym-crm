package com.alirizakaygusuz.gymcrm.security.blacklist;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.blacklist")
@Getter
@Setter
public class TokenBlacklistProperties {

    @NotBlank
    private String redisKeyPrefix;

    @NotNull
    private boolean enabled;
}
