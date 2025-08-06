package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для возврата результата изменения статуса карты")
public class CardSetStatusResponseDTO {

    @Schema(description = "ID карты", example = "101")
    private Long id;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Статус запроса на блокировку", example = "PENDING")
    private BlockRequestStatus blockRequestStatus;
}