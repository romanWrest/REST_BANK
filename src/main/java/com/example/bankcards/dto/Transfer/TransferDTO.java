package com.example.bankcards.dto.Transfer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDTO {
    @NotNull
    private Long Id;
    @NotNull
    private Long fromCardId;

    @NotNull
    private Long toCardId;

    @NotNull
    private LocalDateTime transferTime;

    @NotNull
    @Positive
    private BigDecimal amount;
}
