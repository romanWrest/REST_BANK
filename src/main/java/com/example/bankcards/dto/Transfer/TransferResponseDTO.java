package com.example.bankcards.dto.Transfer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TransferResponseDTO {
    @Schema(description = "Идентификатор трансфера", example = "1")
    private Long id;

    @Schema(description = "Время трансфера", example = "2025-08-02T22:21:56.0940173")
    private LocalDateTime transferTime;

    @Schema(description = "Карта отправителя")
    private Long fromCard;

    @Schema(description = "Карта получателя")
    private Long toCard;

    @Schema(description = "Сумма трансфера", example = "100.50")
    private BigDecimal amount;
}
