package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.BlockRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для возврата результата запроса на блокировку карты")
public class CardResponseBlockDTO {

    @Schema(description = "ID карты", example = "101")
    private Long id;

    @Schema(description = "Статус запроса на блокировку", example = "PENDING")
    private BlockRequestStatus blockRequestStatus;

    public CardResponseBlockDTO(Long id, BlockRequestStatus blockRequestStatus) {
        this.id = id;
        this.blockRequestStatus = blockRequestStatus;
    }
    public CardResponseBlockDTO() {
    }
}