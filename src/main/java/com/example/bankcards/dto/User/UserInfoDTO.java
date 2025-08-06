package com.example.bankcards.dto.User;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO для представления информации о пользователе и его карте")
public class UserInfoDTO {

    @Schema(description = "ID карты", example = "101")
    private Long id;

    @Schema(description = "Маскированный номер карты", example = "**** **** **** 1234")
    private String number;

    @Schema(description = "Информация о пользователе-владельце карты")
    private UserEntity user;

    @PositiveOrZero
    @Schema(description = "Баланс карты", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "Статус запроса на блокировку", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private BlockRequestStatus blockRequestStatus;

    @Schema(description = "Статус карты", example = "ACTIVE", allowableValues = {"ACTIVE", "BLOCKED", "EXPIRED"})
    private CardStatus status;
}