package com.example.bankcards.dto.Transfer;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@FieldNameConstants
public class TransferResponseDTO {
    private LocalDateTime localDateTime;
}
// DTO на выход, данные из БД валидны