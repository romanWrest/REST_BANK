package com.example.bankcards.mappers;

import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.entity.UserEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(UserEntity userEntity);

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.getPassword()))")
    //@Mapping(target = "role", constant = "ROLE_ADMIN")
    UserEntity toEntity(UserRegisterDTO dto, @Context PasswordEncoder passwordEncoder);
}