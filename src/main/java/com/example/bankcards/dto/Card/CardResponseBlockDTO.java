package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import lombok.Data;

@Data
public class CardResponseBlockDTO {
    private Long id;
    private BlockRequestStatus blockRequestStatus;

    public CardResponseBlockDTO(Long id, BlockRequestStatus blockRequestStatus) {
        this.id = id;
        this.blockRequestStatus = blockRequestStatus;
    }
}
