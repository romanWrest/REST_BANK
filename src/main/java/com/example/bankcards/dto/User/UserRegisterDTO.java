package com.example.bankcards.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Size(min = 3, max = 255, message = "Email должен быть от 3 до 255 символов")
    private String email;

    @NotBlank(message = "Полное имя не может быть пустым")
    @Size(max = 255, message = "Полное имя не должно превышать 255 символов")
    private String fullname;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 20, message = "Пароль должен быть от 8 до 20 символов")
    private String password;


}