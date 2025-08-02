package com.example.bankcards.dto.Card;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardResponseBalanceDTO {
    @NotNull
    private Long id;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;
}
