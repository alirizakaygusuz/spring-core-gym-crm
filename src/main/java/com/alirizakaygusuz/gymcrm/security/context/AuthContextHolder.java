package com.alirizakaygusuz.gymcrm.security.context;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthContextHolder {

    private static AuthContext authContext;

    public AuthContextHolder(AuthContext authContext) {
        AuthContextHolder.authContext = authContext;
    }

    public static Optional<String> getAuthenticatedUsername() {
        return Optional.ofNullable(
                authContext != null && authContext.getUsername() != null ?
                        authContext.getUsername() : null
        );
    }

}
