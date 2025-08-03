package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardCreateDTO {
    @NotBlank(message = "Номер карты обязателен")
    @Size(min = 16, max = 19, message = "Номер карты должен содержать 16-19 цифр")
    @Pattern(regexp = "^(\\d{4} ?){3}\\d{4}$", //    @Pattern(regexp = "[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}", message = "Номер карты должен быть в формате XXXX XXXX XXXX XXXX")
            message = "Номер карты должен быть в формате XXXX XXXX XXXX XXXX или XXXXXXXXXXXXXXXX")
    private String number;

    @NotNull(message = "Баланс обязателен")
    @PositiveOrZero(message = "Баланс не может быть отрицательным")
    @Digits(integer = 36, fraction = 2, message = "Баланс должен иметь максимум 36 целых и 2 дробных знака")
    private BigDecimal balance;

    @NotNull(message = "ID пользователя обязательно")
    @Positive(message = "ID пользователя должен быть положительным числом")
    private Long userId;

    @NotNull(message = "Статус карты обязателен")
    private CardStatus status;
}