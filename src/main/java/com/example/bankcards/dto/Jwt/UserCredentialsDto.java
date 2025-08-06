package com.example.bankcards.dto.Jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для передачи учетных данных пользователя для аутентификации")
public class UserCredentialsDto {

    @NotBlank(message = "Полное имя не может быть пустым")
    @Size(max = 255, message = "Полное имя не должно превышать 255 символов")
    @Schema(description = "Полное имя пользователя", example = "John Doe", required = true)
    private String fullName;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 20, message = "Пароль должен быть от 8 до 20 символов")
    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    private String password;
}