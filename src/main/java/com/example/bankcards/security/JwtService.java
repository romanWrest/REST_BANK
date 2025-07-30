package com.example.bankcards.security;

import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtService {
    @Value("91c0d7ada653f590ff8ce22a926fa0ae")
    private String jwtSecret;

    public JwtAuthenticationDto generateAutoToken(String fullName) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(fullName));
        jwtDto.setRefreshToken(generateRefreshJwtToken(fullName));
        return null;
    }

    //public JwtAuthenticationDto refreshBaseToken(fullname)

    private String generateJwtToken(String fullName) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(fullName)
                .expiration(date) // подпись
                .signWith(getSingKey())
                .compact();
    }

    private String generateRefreshJwtToken(String fullName) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder() //рефреш токен валиден 1 день^
                .subject(fullName)
                .expiration(date) // подпись
                .signWith(getSingKey())
                .compact();
    }

    private SecretKey getSingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
