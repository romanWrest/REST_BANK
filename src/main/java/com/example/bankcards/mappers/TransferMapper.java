package com.example.bankcards.mappers;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferEntityDTO;
import com.example.bankcards.dto.Transfer.TransferResponseDTO;
import com.example.bankcards.entity.TransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fromCardIdEntity", source = "fromCardId")
    @Mapping(target = "toCardIdEntity", source = "toCardId")
    @Mapping(target = "transferTime", ignore = true)
    TransferEntity toEntity(TransferDTO dto);

    @Mapping(target = "fromCardId", source = "fromCardIdEntity")
    @Mapping(target = "toCardId", source = "toCardIdEntity")
    TransferEntityDTO toEntityDto(TransferEntity entity);

    @Mapping(target = "localDateTime", source = "transferTime")
    TransferResponseDTO toResponseDto(TransferEntity entity);
}