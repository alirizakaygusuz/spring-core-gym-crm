package com.alirizakaygusuz.gymcrm.client;

import com.alirizakaygusuz.gymcrm.security.authentication.jwt.JwtService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@RequiredArgsConstructor
public class WorkloadServiceFeignConfig {

    @Bean
    public RequestInterceptor workloadAuthInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authHeader = attributes.getRequest().getHeader("Authorization");
                if (authHeader != null) {
                    template.header("Authorization", authHeader);
                }
            }

            String transactionId = MDC.get("transactionId");
            if (transactionId != null) {
                template.header("X-Transaction-Id", transactionId);
            }
        };
    }
}
