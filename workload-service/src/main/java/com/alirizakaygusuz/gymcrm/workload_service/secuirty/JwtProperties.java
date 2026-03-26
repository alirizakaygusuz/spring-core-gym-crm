package com.alirizakaygusuz.gymcrm.workload_service.secuirty;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "security.jwt")
@Getter
@Setter
public class JwtProperties {
    @NotBlank
    private String secret;

}
