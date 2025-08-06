package com.example.bankcards.dto.User;

import com.example.bankcards.entity.enums.RoleUsers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для представления информации о пользователе")
public class UserDTO {

    @Schema(description = "Email пользователя", example = "testuser@example.com")
    private String email;

    @Schema(description = "Полное имя пользователя", example = "John Doe")
    private String fullName;

    @Schema(description = "Роль пользователя", example = "USER")
    private RoleUsers role;
}