package com.example.bankcards.dto.User;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class UserInfoDTO {
    private Long id;

    private String number;

    private UserEntity user;

    private BigDecimal balance;

    private BlockRequestStatus blockRequestStatus;

    private CardStatus status;
}
// DTO на выход, данные из БД валидны