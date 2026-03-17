package com.alirizakaygusuz.gymcrm.workload_service.secuirty;

import com.alirizakaygusuz.gymcrm.workload_service.exception.JwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class JwtService {

    private final JwtProperties jwtProperties;

    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        validateSecret(jwtProperties.getSecret());

        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        log.info("JWT Service initialized with secret of length {} characters ({} bytes)",
                jwtProperties.getSecret().length(),
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8).length);
    }


    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT token is empty/blank: {}", e.getMessage());
            return false;
        }
    }


    public String getUsernameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }




    private void validateSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new JwtTokenException("JWT secret must not be null/blank");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new JwtTokenException("JWT secret must be at least 256 bits (32 bytes) long");
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}