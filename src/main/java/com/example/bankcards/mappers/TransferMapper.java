package com.example.bankcards.mappers;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.util.MaskingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransferMapper {
    TransferMapper INSTANCE = Mappers.getMapper(TransferMapper.class);


    @Mapping(target = "maskedNumber", source = "number", qualifiedByName = "maskedNumberMapper")
    CardDTO toCardDTO(CardEntity cardEntity);

    @Named("maskedNumberMapper")
    static String maskedNumberMapper(String number) {
        return MaskingUtil.maskCardNumber(number);
    }
}
