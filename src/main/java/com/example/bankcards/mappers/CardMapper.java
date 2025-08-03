package com.example.bankcards.mappers;

import com.example.bankcards.dto.Card.CardCreateDTO;
import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Card.CardResponseRequestStatusDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.util.MaskingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mappings({
            @Mapping(source = "number", target = "maskedNumber", qualifiedByName = "maskedNumberMapper"),
            @Mapping(source = CardEntity.Fields.status, target = CardDTO.Fields.status),
    })
    CardDTO toCardDTO(CardEntity cardEntity);


    @Named("maskedNumberMapper")
    static String maskedNumberMapper(String number) {
        return MaskingUtil.maskCardNumber(number);
    }

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    CardEntity toEntity(CardCreateDTO dto);

    @Mappings({
            //   @Mapping(source = "number", target = "number", ignore = true), // указанных target нет в итоговом DTO
            //   @Mapping(source = "balance", target = "balance", ignore = true)// но так требует mapstruct
    })
    CardResponseRequestStatusDTO toCardResponseRequestStatusDTO(CardEntity cardEntity);
}