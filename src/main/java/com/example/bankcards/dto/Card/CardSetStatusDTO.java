package com.example.bankcards.dto.Card;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

@Data
public class CardSetStatusDTO {
    @NotNull
    private Long id;

    @NotNull
    @ValidEnum(enumClass = CardStatus.class, allowedValues = {"ACTIVE", "BLOCK", "EXPIRED"})
    private CardStatus status;
}


@Constraint(validatedBy = EnumValidatorConstraint.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@interface ValidEnum {
    String message() default "Invalid CardStatus value. Allowed values are: ACTIVE, BLOCK, EXPIRED";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Enum<?>> enumClass();
    String[] allowedValues();
}


class EnumValidatorConstraint implements ConstraintValidator<ValidEnum, Enum<?>> {
    private List<String> allowedValues;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        allowedValues = Arrays.asList(constraintAnnotation.allowedValues());
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull проверит null отдельно
        }
        return allowedValues.contains(value.name());
    }
}