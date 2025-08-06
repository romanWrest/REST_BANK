package com.example.bankcards.dto.Transfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO для выполнения перевода между банковскими картами")
public class TransferDTO {

    @NotNull(message = "ID пользователя обязательно")
    @Positive(message = "ID пользователя должен быть положительным числом")
    @Schema(description = "ID пользователя, инициирующего перевод", example = "1", required = true)
    private Long userId;

    @NotNull(message = "ID исходной карты обязательно")
    @Positive(message = "ID исходной карты должен быть положительным числом")
    @Schema(description = "ID карты, с которой выполняется перевод", example = "101", required = true)
    private Long fromCardId;

    @NotNull(message = "ID целевой карты обязательно")
    @Positive(message = "ID целевой карты должен быть положительным числом")
    @Schema(description = "ID карты, на которую выполняется перевод", example = "102", required = true)
    private Long toCardId;

    @NotNull(message = "Сумма перевода обязательна")
    @Positive(message = "Сумма перевода должна быть положительной")
    @Digits(integer = 36, fraction = 2, message = "Сумма должна иметь максимум 36 целых и 2 дробных знака")
    @Schema(description = "Сумма перевода", example = "500.00", required = true)
    private BigDecimal amount;
}