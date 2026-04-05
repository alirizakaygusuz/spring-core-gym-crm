package com.alirizakaygusuz.gymcrm.security.ratelimit;

import com.alirizakaygusuz.gymcrm.dto.auth.LoginRequest;
import com.alirizakaygusuz.gymcrm.exception.RateLimitExceededException;
import com.alirizakaygusuz.gymcrm.security.web.MultiReadHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginRateLimitInterceptor implements HandlerInterceptor {

    private final LoginRateLimitService loginRateLimiter;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!"/api/v1/login".equals(request.getRequestURI()) || !"POST".equals(request.getMethod())) {
            return true;
        }

        try {
            String username = extractUsername(request);

            if (username != null && !username.isBlank()) {
                loginRateLimiter.validateOrThrow(username);
            }

        } catch (RateLimitExceededException e) {
            log.warn("Rate limit validation failed: {}", e.getMessage());
            throw e;
        }

        return true;
    }

    private String extractUsername(HttpServletRequest request) {
        Object cached = request.getAttribute("cachedAuthRequest");

        if (!(cached instanceof MultiReadHttpServletRequest wrapper)) {
            return null;
        }

        try {
            byte[] body = wrapper.getCachedBody();
            if (body.length == 0) return null;
            LoginRequest loginRequest = objectMapper.readValue(body, LoginRequest.class);
            return loginRequest.username();
        } catch (Exception e) {
            log.debug("Could not extract username from request body", e);
            return null;
        }
    }
}