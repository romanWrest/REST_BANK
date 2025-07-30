package com.example.bankcards.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
// такое название однозначно говорит, что это дто на вход
public class UserRegisterRequestDTO {
    @NotNull
    @Email
    @Size(min = 3, max = 50)
    private String email;

    @NotNull
    @Size(min = 3, max = 50)
    private String fullName;

    @NotNull
    @Size(min = 10, max = 11)
    private String phoneNumber;

    @NotNull
    @Size(min = 8, message = "Длина пароля должна быть не менее 8 символов")
    private String password;
}