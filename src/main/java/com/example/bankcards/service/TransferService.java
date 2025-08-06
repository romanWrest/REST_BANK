package com.example.bankcards.service;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferResponseDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.TransferEntity;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.util.CardValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;


    @Transactional
    public TransferResponseDTO transfer(TransferDTO transferDTO) {
        log.info("Начало операции перевода. TransferDTO: {}", transferDTO);

        log.debug("Поиск карты отправителя с ID: {}", transferDTO.getFromCardId());
        CardEntity fromCard = getCardEntity(transferDTO.getFromCardId());
        log.info("Карта отправителя найдена: {}", fromCard);

        log.debug("Поиск карты получателя с ID: {}", transferDTO.getToCardId());
        CardEntity toCard = getCardEntity(transferDTO.getToCardId());
        log.info("Карта получателя найдена: {}", toCard);

        CardValidationUtil.validateSameUserTransfer(fromCard, toCard);
        CardValidationUtil.validateCardsBelongToUser(transferDTO, fromCard, toCard);
        CardValidationUtil.validateStatusesCards(fromCard, toCard);
        CardValidationUtil.validateBalanceCard(transferDTO, fromCard, toCard);

        log.debug("Обновление баланса карты отправителя. Текущий баланс: {}, Сумма перевода: {}", fromCard.getBalance(), transferDTO.getAmount());
        fromCard.setBalance(fromCard.getBalance().subtract(transferDTO.getAmount()));
        log.debug("Обновление баланса карты получателя. Текущий баланс: {}, Сумма перевода: {}", toCard.getBalance(), transferDTO.getAmount());
        toCard.setBalance(toCard.getBalance().add(transferDTO.getAmount()));

        log.debug("Сохранение изменений карт в базе данных");
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        log.info("Балансы карт обновлены. Новый баланс карты отправителя: {}, Новый баланс карты получателя: {}",
                fromCard.getBalance(), toCard.getBalance());

        log.debug("Создание сущности перевода");
        TransferEntity transferEntity = new TransferEntity();
        transferEntity.setFromCard(fromCard);
        transferEntity.setToCard(toCard);
        transferEntity.setAmount(transferDTO.getAmount());
        transferEntity.setTransferTime(LocalDateTime.now());
        log.debug("Сохранение перевода в базе данных");
        transferRepository.save(transferEntity);
        log.info("Перевод успешно сохранен: {}", transferEntity);
        TransferResponseDTO transferResponseDTO = new TransferResponseDTO();
        transferResponseDTO.setId(transferEntity.getId());
        transferResponseDTO.setTransferTime(transferEntity.getTransferTime());
        transferResponseDTO.setFromCard(transferDTO.getFromCardId());
        transferResponseDTO.setToCard(transferDTO.getToCardId());
        transferResponseDTO.setAmount(transferDTO.getAmount());
        log.info("Операция перевода завершена успешно. Результат: {}", transferEntity);
        return transferResponseDTO;
    }

    private CardEntity getCardEntity(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Карта с ID {} не найдена", id);
                    return new CardNotFoundException("Карта не найдена с ID: " + id, 404);
                });
    }
}