package com.example.bankcards.dto.Jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для возврата JWT-токена и refresh-токена после аутентификации")
public class JwtAuthenticationDto {

    @Schema(description = "JWT-токен (алгоритм HS384, содержит sub, role, exp)",
            example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0dXNlckBleGFtcGxlLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE3NTQ0NDMwMjB9.s3iml83m36wdz447MYQ6LrNjXmtuvffZIs9U51huJxABv9rLo59I2ZJNLlKQ6nkA",
            required = true)
    private String token;

    @Schema(description = "Refresh-токен для обновления JWT",
            example = "dGhpcy1pcy1hLXJlZnJlc2gtdG9rZW4=",
            required = true)
    private String refreshToken;
}