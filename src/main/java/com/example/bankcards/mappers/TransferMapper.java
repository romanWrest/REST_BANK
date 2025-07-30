package com.example.bankcards.mappers;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.util.EncryptionService;
import org.springframework.stereotype.Component;

// Мапперы двигаем в пакет мапперов. И по аналогии с CardMapper
@Component
public class TransferMapper {
    private final EncryptionService encryptionService;

    public TransferMapper(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public TransferDTO toTransferDTO(Transfer transfer) {
        TransferDTO dto = new TransferDTO();
        dto.setId(transfer.getId());
        dto.setFromCardId(transfer.getFromCard().getId());
        dto.setToCardId(transfer.getToCard().getId());
        dto.setAmount(transfer.getAmount());
        dto.setTransferTime(transfer.getTransferTime());
        return dto;
    }
}