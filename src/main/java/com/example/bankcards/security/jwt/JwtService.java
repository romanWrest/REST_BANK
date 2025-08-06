package com.example.bankcards.security.jwt;

import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
@Slf4j
public class JwtService {
    @Value("c362b68d0793bd37b7b5252f250d4abbe02e671cd98d725d73d63bfd2ca3bda3417c7443")
    public String jwtSecret;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public JwtAuthenticationDto generateAutoToken(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email, user.getRole().name()));
        jwtDto.setRefreshToken(generateRefreshJwtToken(email, user.getRole().name()));
        return jwtDto;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email, user.getRole().name()));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Expired JwtException", expEx);
        } catch (UnsupportedJwtException expEx) {
            log.error("Unsupported Jwt Exception", expEx);
        } catch (MalformedJwtException expEx) {
            log.error("Malformed Jwt Exception", expEx);
        } catch (SecurityException expEx) {
            log.error("Security Exception", expEx);
        } catch (Exception expEx) {
            log.error("Token invalid", expEx);
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return (String) claims.get("role");
    }

    public String generateJwtToken(String email, String role) {
        Date date = Date.from(LocalDateTime.now().plusHours(24).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .expiration(date)
                .signWith(getSignKey())
                .compact();
    }

    private String generateRefreshJwtToken(String email, String role) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .expiration(date)
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid BASE64 secret key: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to decode JWT secret key", e);
        }
    }
}