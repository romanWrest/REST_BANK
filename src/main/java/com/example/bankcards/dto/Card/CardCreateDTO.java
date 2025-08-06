package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO для создания новой банковской карты")
public class CardCreateDTO {

    @NotBlank(message = "Номер карты обязателен")
    @Size(min = 16, max = 19, message = "Номер карты должен содержать 16-19 цифр")
    @Pattern(regexp = "^(\\d{4} ?){3}\\d{4}$", message = "Номер карты должен быть в формате XXXX XXXX XXXX XXXX или без пробелов между группами цифр")
    @Schema(description = "Номер карты (16 цифр, с пробелами или без)", example = "1234 5678 9012 3456", required = true)
    private String number;

    @NotNull(message = "Баланс обязателен")
    @PositiveOrZero(message = "Баланс не может быть отрицательным")
    @Digits(integer = 36, fraction = 2, message = "Баланс должен иметь максимум 36 целых и 2 дробных знака")
    @Schema(description = "Начальный баланс карты 100", example = "100.00", required = true)
    private BigDecimal balance;

    @NotNull(message = "ID пользователя обязательно")
    @Positive(message = "ID пользователя должен быть положительным числом")
    @Schema(description = "ID пользователя-владельца карты", example = "1", required = true)
    private Long userId;

    @NotNull(message = "Статус карты обязателен")
    @Schema(description = "Статус карты", example = "ACTIVE", required = true)
    private CardStatus status;
}