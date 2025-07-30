package com.example.bankcards.dto.Jwt;

import lombok.Data;
@Data
public class JwtAuthenticationDto {
    private String token;
    private String refreshToken;
}
