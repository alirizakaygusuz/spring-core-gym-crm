package com.alirizakaygusuz.gymcrm.workload_service.secuirty.config;

import com.alirizakaygusuz.gymcrm.workload_service.secuirty.JwtAuthenticationFilter;
import com.alirizakaygusuz.gymcrm.workload_service.secuirty.web.RestAccessDeniedHandler;
import com.alirizakaygusuz.gymcrm.workload_service.secuirty.web.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;


    private static final String[] PUBLIC_ENDPOINTS = {
            "/h2-console/**",
            // Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",

            // Ops
            "/actuator/**",

            // Test-only reset endpoint. Safe to expose publicly because the backing
            // controller (@Profile("integration")) is only loaded when the
            // "integration" profile is active
            "/api/v1/test/reset"

    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}