package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@FieldNameConstants
public class CardDTO {
    @NotNull
    private Long id;

    @NotNull
    private String maskedNumber;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    private CardStatus status;
}

//иногда ответный, иногда промежуточный -> поэтому валидация обеспечивается maskedNumber маскируется)))
// АНГЕЛИНА если это коряво дай пожалуйста знать!!!!!
// -> пересмотрю DTO и сделаю так чтобы DTO на выход были без валидации чистые, а на вход с валидацией