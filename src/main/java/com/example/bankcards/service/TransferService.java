package com.example.bankcards.service;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferEntityDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.TransferEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.mappers.TransferMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;

    public TransferService(CardRepository cardRepository, TransferRepository transferRepository, TransferMapper transferMapper) {
        this.cardRepository = cardRepository;
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
    }

    @Transactional
    public TransferEntityDTO transfer(TransferDTO transferDTO) {
        CardEntity fromCard = cardRepository.findById(transferDTO.getFromCardId())
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + transferDTO.getFromCardId()));
        CardEntity toCard = cardRepository.findById(transferDTO.getToCardId())
                .orElseThrow(() -> new CardNotFoundException("Card not found with ID: " + transferDTO.getToCardId()));

        if (!fromCard.getUser().getId().equals(toCard.getUser().getId())) {
            throw new IllegalArgumentException("Переводы можно совершать только на свою карту");
        }

        if (!fromCard.getUser().getId().equals(transferDTO.getUserId()) || !toCard.getUser().getId().equals(transferDTO.getUserId())) {
            throw new IllegalArgumentException("Карты должны принадлежать указанному пользователю");
        }


        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Карта заблокирована, обратитесь в банк");
        }

        if (fromCard.getBalance().compareTo(transferDTO.getAmount()) < 0) {
            throw new IllegalArgumentException("Сумма перевода не должна превышать текущий баланс");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(transferDTO.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transferDTO.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        TransferEntity transferEntity = transferMapper.toEntity(transferDTO);
        transferEntity.setFromCardIdEntity(fromCard.getId());
        transferEntity.setToCardIdEntity(toCard.getId());
        transferEntity.setTransferTime(LocalDateTime.now());

        transferRepository.save(transferEntity);

        return transferMapper.toEntityDto(transferEntity);
    }
}
