package com.alirizakaygusuz.gymcrm.security.filter;

import com.alirizakaygusuz.gymcrm.security.context.AuthContextImpl;
import com.alirizakaygusuz.gymcrm.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthContextImpl authContext;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        response.setHeader("X-Transaction-Id", transactionId);


        log.info("REST IN  {} {}", request.getMethod(), request.getRequestURI());

        try{

            if(isPublicEndpoint(request)){
                log.info("REST IN  {} {} (public)", request.getMethod(), request.getRequestURI());

                filterChain.doFilter(request, response);
                log.info("REST OUT {} {} -> {}",
                        request.getMethod(), request.getRequestURI(), response.getStatus());
                return;
            }


            String jwt = getJwtFromRequest(request);

            if (jwt == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Missing token");
                log.warn("REST OUT {} {} -> {} ({})",
                        request.getMethod(), request.getRequestURI(), response.getStatus(),
                        "Missing token");
                return;
            }

            if (!jwtService.validateToken(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                log.warn("REST OUT {} {} -> {} ({})",
                        request.getMethod(), request.getRequestURI(), response.getStatus(),
                        "Invalid token");
                return;
            }

            String username = jwtService.getUsernameFromToken(jwt);

            authContext.setUsername(username);
            authContext.setAuthenticated(true);

            log.info("REST IN  {} {} User: {}",
                    request.getMethod(), request.getRequestURI(), username);

            filterChain.doFilter(request, response);

            log.info("REST OUT {} {} -> {}",
                    request.getMethod(), request.getRequestURI(), response.getStatus());

        }finally {
            MDC.clear();
        }

    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        return isMatch(method, uri, "POST", "/api/v1/login")
                || isMatch(method, uri, "POST", "/api/v1/trainees")
                || isMatch(method, uri, "POST", "/api/v1/trainers")
                || isMatch(method, uri, "GET", "/api/v1/trainings/types")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/webjars")
                || uri.startsWith("/actuator");

    }


    private boolean isMatch(String actualMethod, String actualUri,
                            String expectedMethod, String expectedUri) {
        return expectedMethod.equalsIgnoreCase(actualMethod)
                && expectedUri.equals(actualUri);
    }
}




















