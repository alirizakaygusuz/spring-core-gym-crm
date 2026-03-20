package com.alirizakaygusuz.gymcrm.security.authentication.logout;

import com.alirizakaygusuz.gymcrm.security.blacklist.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutInterceptor implements HandlerInterceptor {

    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            tokenBlacklistService.blacklist(token);
            log.info("Token automatically blacklisted via LogoutInterceptor");
        } else {
            log.warn("Logout attempt without valid Authorization header");
        }

        return true;
    }

}
