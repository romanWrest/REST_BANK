package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Banks;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardCreateDTO {
    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен иметь размер 16 цифр")
    private String number;

    @NotNull
    @Size(min = 3, max = 50)
    private String owner;

    @NotNull
    private LocalDate expiryDate;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    private Banks bank;

    @NotNull
    private Long userId;
}