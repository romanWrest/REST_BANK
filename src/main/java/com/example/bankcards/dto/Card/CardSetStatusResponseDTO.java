package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardSetStatusResponseDTO {
    @NotNull
    private Long id;

    private CardStatus status;
    
    private BlockRequestStatus blockRequestStatus;
}
