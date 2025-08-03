package com.example.bankcards.dto.Transfer;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDTO {
    @NotNull(message = "ID пользователя обязательно")
    @Positive(message = "ID пользователя должен быть положительным числом")
    private Long userId;

    @NotNull(message = "ID исходной карты обязательно")
    @Positive(message = "ID исходной карты должен быть положительным числом")
    private Long fromCardId;

    @NotNull(message = "ID целевой карты обязательно")
    @Positive(message = "ID целевой карты должен быть положительным числом")
    private Long toCardId;

    @NotNull(message = "Сумма перевода обязательна")
    @Positive(message = "Сумма перевода должна быть положительной")
    @Digits(integer = 36, fraction = 2, message = "Сумма должна иметь максимум 36 целых и 2 дробных знака")
    private BigDecimal amount;

    @NotNull(message = "Время перевода обязательно")
    @FutureOrPresent(message = "Время перевода не может быть в прошлом")
    private LocalDateTime transferTime;
}