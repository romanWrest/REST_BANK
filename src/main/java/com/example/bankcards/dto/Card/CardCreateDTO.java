package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardCreateDTO {
    @NotNull
    @Pattern(regexp = "[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}", message = "Номер карты должен быть в формате XXXX XXXX XXXX XXXX")
    private String number;


    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    private Long userId;

    private CardStatus status;
}