package com.example.bankcards.dto.User;

import com.example.bankcards.entity.enums.RoleUsers;
import lombok.Data;

@Data
public class UserDTO {

    private String email;

    private String fullName;

    private RoleUsers role;
}

//Отсутсвие валидации обосновывается тем, что это DTO предназначено на выход
// а данные из БД уже валидны