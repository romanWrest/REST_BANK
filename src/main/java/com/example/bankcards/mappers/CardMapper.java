package com.example.bankcards.mappers;

import com.example.bankcards.dto.Card.CardCreateDTO;
import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.util.MaskingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CardMapper {

    // Все поля с одинаковыми типами и именами mapstruct мапит сам. С разными именами - достаточно указать source и target.
    // qualifiedByName - это если для маппинга требуется функция
    @Mappings({
            @Mapping(source = "number", target = "maskedNumber", qualifiedByName = "maskedNumberMapper"),
    })
    CardDTO toCardDTO(CardEntity cardEntity);

    @Named("maskedNumberMapper")
    static String maskedNumberMapper(String number) {
        return MaskingUtil.maskCardNumber(number);
    }

    // так как все поля совпадают, то ничего делать не нужно
    @Mapping(target = "id", ignore = true)
    CardEntity toEntity(CardCreateDTO dto);

}