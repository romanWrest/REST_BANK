package com.example.bankcards.dto.Transfer;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferEntityDTO {
    private Long userId;

    private Long fromCardId;

    private Long toCardId;

    private LocalDateTime transferTime;

    private BigDecimal amount;
}

// Промежуточная между входом и БД, ответсвенность за валидность полностью обеспечена TransferDTO
// package com.example.bankcards.dto.Transfer.TransferDTO;