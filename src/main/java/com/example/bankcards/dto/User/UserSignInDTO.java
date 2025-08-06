package com.example.bankcards.dto.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для входа пользователя")
public class UserSignInDTO {

    @Size(min = 3, max = 20, message = "Email должен быть от 3 до 20 символов")
    @Schema(description = "Email пользователя", example = "testuser@example.com", required = true)
    private String email;

    @Size(min = 8, max = 20, message = "Пароль должен быть от 8 до 20 символов")
    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    private String password;

    public UserSignInDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserSignInDTO() {

    }
}