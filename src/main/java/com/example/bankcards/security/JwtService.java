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

    public JwtAuthenticationDto generateAutoToken(String email) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(generateRefreshJwtToken(email));
        return null;
    }

    //public JwtAuthenticationDto refreshBaseToken(fullname)

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
