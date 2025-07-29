package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CardUpdateDTO {
    @NotNull
    private CardStatus status;

    private LocalDate expiryDate;
}
