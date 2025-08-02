package com.example.bankcards.security.jwt;

import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Component
public class JwtService {
    private static final Logger log = LogManager.getLogger(JwtService.class);
    @Value("c362b68d0793bd37b7b5252f250d4abbe02e671cd98d725d73d63bfd2ca3bda3417c7443")
    private String jwtSecret;

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
                    .verifyWith(getSingKey())
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
                .verifyWith(getSingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return (String) claims.get("role");
    }

    private String generateJwtToken(String email, String role) {
        Date date = Date.from(LocalDateTime.now().plusHours(24).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .expiration(date)
                .signWith(getSingKey())
                .compact();
    }

    private String generateRefreshJwtToken(String email, String role) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .expiration(date)
                .signWith(getSingKey())
                .compact();
    }

    private SecretKey getSingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}