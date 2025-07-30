package com.example.bankcards.dto.Jwt;

import lombok.Data;

@Data
public class UserCredentialsDto {
    private String fullName;
    private String password;
}
