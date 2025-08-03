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
    @Mapping(target = TransferEntity.Fields.fromCard, source = TransferDTO.Fields.fromCardId)
    @Mapping(target = TransferEntity.Fields.toCard, source = "toCardId")
    @Mapping(target = TransferEntity.Fields.transferTime, ignore = true)
    TransferEntity toEntity(TransferDTO dto);

    @Mapping(target = TransferEntityDTO.Fields.fromCardId, source = TransferEntity.Fields.fromCard)
    @Mapping(target = TransferEntityDTO.Fields.toCardId, source = TransferEntity.Fields.toCard)
    TransferEntityDTO toEntityDto(TransferEntity entity);

    @Mapping(target = TransferEntity.Fields.transferTime, source = TransferEntity.Fields.transferTime)
    TransferResponseDTO toResponseDto(TransferEntity entity);
}