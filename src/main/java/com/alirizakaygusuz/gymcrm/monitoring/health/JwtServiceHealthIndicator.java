package com.alirizakaygusuz.gymcrm.monitoring.health;

import com.alirizakaygusuz.gymcrm.security.jwt.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("jwtServiceHealth")
@RequiredArgsConstructor
public class JwtServiceHealthIndicator implements HealthIndicator {

    private  final JwtService jwtService;

    @Override
    public Health health() {

        try {
            String testToken = jwtService.generateToken("health-check");
            boolean isValid = jwtService.validateToken(testToken);

            if (isValid) {
                return Health.up()
                        .withDetail("service", "JWT Authentication")
                        .withDetail("status", "Operational")
                        .withDetail("testTokenGeneration", "Success")
                        .withDetail("testTokenValidation", "Success")
                        .build();
            } else {
                return Health.down()
                        .withDetail("service", "JWT Authentication")
                        .withDetail("status", "Token validation failed")
                        .withDetail("testTokenGeneration", "Success")
                        .withDetail("testTokenValidation", "Failed")
                        .build();
            }
        } catch (JwtException e) {
            log.error("JwtService health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("service", "JWT Authentication")
                    .withDetail("status", "Error during token generation/validation")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
