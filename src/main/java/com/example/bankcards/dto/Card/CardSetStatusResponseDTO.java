package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import lombok.Data;

@Data
public class CardSetStatusResponseDTO {
    private Long id;

    private CardStatus status;

    private BlockRequestStatus blockRequestStatus;
}


// ответное DTO
// создается в сервисе и сразу идет на ответ, после принятия данных из
// package com.example.bankcards.dto.Card.CardSetStatusDTO - валидция полностью обеспечена этим парнем
