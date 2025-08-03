package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import lombok.Data;

@Data
public class CardResponseRequestStatusDTO {
    private Long id;

    private UserEntity user;
    private BlockRequestStatus blockRequestStatus;

    private CardStatus status;
}
// DTO на выход