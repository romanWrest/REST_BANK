package com.example.bankcards.util;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;

@Mapper
public class CardMapper {
    public static CardDTO toCardDTO(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setMaskedNumber(MaskingUtil.maskCardNumber(card.getNumber()));
        dto.setOwner(card.getOwner());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setBalance(card.getBalance());
        dto.setStatus(card.getStatus());
        dto.setBank(card.getBank());
        return dto;
    }
}