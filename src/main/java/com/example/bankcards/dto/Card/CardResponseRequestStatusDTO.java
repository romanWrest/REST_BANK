package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "DTO для возврата статуса запроса на блокировку карты с информацией о пользователе")
public class CardResponseRequestStatusDTO {

    @Schema(description = "ID карты", example = "101")
    private Long id;

    @Schema(description = "Маскированный номер карты", example = "**** **** **** 1234")
    private String maskedNumber;

    @Schema(description = "Информация о пользователе-владельце карты")
    private UserEntity user;

    @Schema(description = "Статус запроса на блокировку", example = "PENDING")
    private BlockRequestStatus blockRequestStatus;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;
}