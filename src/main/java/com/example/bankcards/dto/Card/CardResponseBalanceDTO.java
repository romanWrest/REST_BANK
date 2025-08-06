package com.example.bankcards.dto.Card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO для возврата баланса банковской карты")
public class CardResponseBalanceDTO {

    @Schema(description = "ID карты", example = "101")
    private Long id;

    @Schema(description = "Баланс карты", example = "1000.00")
    private BigDecimal balance;
}