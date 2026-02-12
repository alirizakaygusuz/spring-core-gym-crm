package com.alirizakaygusuz.gymcrm.filter;

import com.alirizakaygusuz.gymcrm.service.auth.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    public static final String AUTH_USER_ATTR = "authenticatedUser";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        response.setHeader("X-Transaction-Id", transactionId);

        log.info("REST IN  {} {}", request.getMethod(), request.getRequestURI());



        try {

            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);

                log.info("REST OUT {} {} -> {}",
                        request.getMethod(), request.getRequestURI(), response.getStatus());
                return;
            }

            String username = request.getHeader("X-Username");
            String password = request.getHeader("X-Password");

            if (username == null || password == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Missing credentials");

                log.warn("REST OUT {} {} -> {} ({})",
                        request.getMethod(), request.getRequestURI(), response.getStatus(),
                        "Missing credentials");
                return;
            }

            boolean authenticated = authService.authenticate(username, password);

            if (!authenticated) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid username or password");

                log.warn("REST OUT {} {} -> {} ({})",
                        request.getMethod(), request.getRequestURI(), response.getStatus(),
                        "Invalid username or password");
                return;
            }

            request.setAttribute(AUTH_USER_ATTR, username);
            filterChain.doFilter(request, response);

            log.info("REST OUT {} {} -> {}",
                    request.getMethod(), request.getRequestURI(), response.getStatus());

        } finally {
            MDC.clear();
        }
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        return isMatch(method, uri, "POST", "/api/v1/trainees")
                || isMatch(method, uri, "POST", "/api/v1/trainers")
                || isMatch(method, uri, "GET", "/api/v1/login")

                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui");


    }

    private boolean isMatch(
            String actualMethod,
            String actualUri,
            String expectedMethod,
            String expectedUri
    ) {
        return expectedMethod.equalsIgnoreCase(actualMethod)
                && expectedUri.equals(actualUri);
    }
}
