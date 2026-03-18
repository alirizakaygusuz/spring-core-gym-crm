package com.alirizakaygusuz.gymcrm.workload_service.secuirty;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String transactionId = request.getHeader("X-Transaction-Id");

        if(transactionId == null  ||  transactionId.isBlank()){
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put("transactionId", transactionId);
        response.setHeader("X-Transaction-Id", transactionId);


        log.info("REST IN  {} {}", request.getMethod(), request.getRequestURI());

        try {

            String jwt = getJwtFromRequest(request);

            if (jwt != null && jwtService.isTokenValid(jwt)) {


                String username = jwtService.getUsernameFromToken(jwt);
                List<String> roles = jwtService.getRolesFromToken(jwt);

                List<GrantedAuthority>  authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                var authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("User authenticated: {} with roles: {}", username, authorities);

            }

            filterChain.doFilter(request, response);


            log.info("REST OUT {} {} -> {}",
                    request.getMethod(), request.getRequestURI(), response.getStatus());

        } catch (Exception e) {
            log.error("Error in JWT authentication filter: {}", e.getMessage(), e);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
            SecurityContextHolder.clearContext();
        }

    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }


}