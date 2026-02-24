package com.alirizakaygusuz.gymcrm.security.authentication.jwt;

import com.alirizakaygusuz.gymcrm.exception.JwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        validateSecret(jwtProperties.getSecret());
        validateTokenExpiration(jwtProperties.getExpiration());

        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        log.info("JWT Service initialized with secret length {} and expiration {} ms",
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8).length,
                jwtProperties.getExpiration());
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateToken(String username) {
        return  generateToken(username, List.of());
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

    public long getExpirationTime() {
        return jwtProperties.getExpiration();
    }

    private void validateTokenExpiration(long expiration) {
        if (expiration <= 0) {
            throw new JwtTokenException("JWT token expiration time must be greater than 0");
        }
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