package com.example.bankcards.dto.Jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO для передачи refresh-токена для обновления JWT")
public class RefreshTokenDto {

    @NotBlank(message = "Refresh-токен не может быть пустым")
    @Schema(description = "Refresh-токен для обновления JWT",
            example = "dGhpcy1pcy1hLXJlZnJlc2gtdG9rZW4=",
            required = true)
    private String refreshToken;
}