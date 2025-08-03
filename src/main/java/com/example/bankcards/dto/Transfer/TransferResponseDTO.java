package com.example.bankcards.dto.Transfer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferResponseDTO {
    private LocalDateTime localDateTime;
}
// DTO на выход, данные из БД валидны