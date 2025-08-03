package com.example.bankcards.dto.Card;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardResponseBalanceDTO {
    private Long id;

    private BigDecimal balance;
}
// DTO на выход, данные из БД валидны