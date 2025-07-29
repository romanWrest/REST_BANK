package com.example.bankcards.dto.User;

import com.example.bankcards.entity.enums.RoleUsers;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 10, max = 11)
    private String phoneNumber;

    @NotNull
    private RoleUsers role;
}