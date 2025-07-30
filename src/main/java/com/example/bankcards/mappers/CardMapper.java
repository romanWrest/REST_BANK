package com.example.bankcards.mappers;

import com.example.bankcards.dto.Card.CardCreateDTO;
import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.MaskingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring") // неправильно вы, дядь Федор, мапстракт кушаете. Он для упрощения работы, а не усложнения)
public interface CardMapper {

    // Все поля с одинаковыми типами и именами mapstruct мапит сам. С разными именами - достаточно указать source и target.
    // qualifiedByName - это если для маппинга требуется функция
    @Mappings({
            @Mapping(source = "number", target = "maskedNumber", qualifiedByName = "maskedNumberMapper"),
    })
    CardDTO toCardDTO(Card card);

    @Named("maskedNumberMapper")
    static String maskedNumberMapper(String number) {
        return MaskingUtil.maskCardNumber(number);
    }

    // так как все поля совпадают, то ничего делать не нужно
    Card toEntity(CardCreateDTO dto);

}

//public class CardMapper {
//    public static CardDTO toCardDTO(Card card) {
//        CardDTO dto = new CardDTO();
//        dto.setId(card.getId());
//        dto.setMaskedNumber(MaskingUtil.maskCardNumber(card.getNumber()));
//        dto.setOwner(card.getOwner());
//        dto.setExpiryDate(card.getExpiryDate());
//        dto.setBalance(card.getBalance());
//        dto.setStatus(card.getStatus());
//        dto.setBank(card.getBank());
//        return dto;
//    }
//}