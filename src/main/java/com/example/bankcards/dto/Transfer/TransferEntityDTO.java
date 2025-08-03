package com.example.bankcards.dto.Transfer;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldNameConstants
public class TransferEntityDTO {
    private Long userId;

    private Long fromCardId;

    private Long toCardId;

    private LocalDateTime transferTime;

    private BigDecimal amount;
}

// Промежуточная между входом и БД, ответсвенность за валидность полностью обеспечена TransferDTO
// package com.example.bankcards.dto.Transfer.TransferDTO;