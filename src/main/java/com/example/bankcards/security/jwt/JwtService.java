package com.example.bankcards.security.jwt;

import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
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
    @Value("91c0d7ada653f590ff8ce22a926fa0ae")
    private String jwtSecret;

    public JwtAuthenticationDto generateAutoToken(String email) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(generateRefreshJwtToken(email));
        return null;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken) { // когда токен пеестанет быть валидным, произойдет обновление
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().
                    verifyWith(getSingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Expired JwtException", expEx);
        } catch (UnsupportedJwtException ExpEx) {
            log.error("Unsupported Jwt Exception", ExpEx);
        } catch (MalformedJwtException expEx) {
            log.error("Malformed Jwt Exception", expEx);
        } catch (SecurityException expEx) {
            log.error("Security Exception", expEx);
        } catch (Exception expEx) {
            log.error("Token unvalid", expEx);
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        Claims claims = Jwts.parser()
                .verifyWith(getSingKey())
                .build()
                .parseEncryptedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private String generateJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date) // подпись
                .signWith(getSingKey())
                .compact();
    }

    private String generateRefreshJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder() //рефреш токен валиден 1 день^
                .subject(email)
                .expiration(date) // подпись
                .signWith(getSingKey())
                .compact();
    }

    private SecretKey getSingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
