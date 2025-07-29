package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.Banks;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardDTO {
    @NotNull
    private Long id;

    @NotNull
    @Size(min = 16, max = 16)
    private String maskedNumber;

    @NotNull
    @Size(min = 3, max = 50)
    private String owner;

    @NotNull
    private LocalDate expiryDate;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    private CardStatus status;

    @NotNull
    private Banks bank;
}