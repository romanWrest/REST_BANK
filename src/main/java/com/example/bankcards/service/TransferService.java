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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;

    public TransferService(CardRepository cardRepository, TransferRepository transferRepository, TransferMapper transferMapper) {
        this.cardRepository = cardRepository;
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
        logger.info("Инициализация сервиса TransferService");
    }

    @Transactional
    public TransferEntityDTO transfer(TransferDTO transferDTO) {
        logger.info("Начало операции перевода. TransferDTO: {}", transferDTO);

        logger.debug("Поиск карты отправителя с ID: {}", transferDTO.getFromCardId());
        CardEntity fromCard = cardRepository.findById(transferDTO.getFromCardId())
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", transferDTO.getFromCardId());
                    return new CardNotFoundException("Карта не найдена с ID: " + transferDTO.getFromCardId());
                });
        logger.info("Карта отправителя найдена: {}", fromCard);

        logger.debug("Поиск карты получателя с ID: {}", transferDTO.getToCardId());
        CardEntity toCard = cardRepository.findById(transferDTO.getToCardId())
                .orElseThrow(() -> {
                    logger.error("Карта с ID {} не найдена", transferDTO.getToCardId());
                    return new CardNotFoundException("Карта не найдена с ID: " + transferDTO.getToCardId());
                });
        logger.info("Карта получателя найдена: {}", toCard);

        logger.debug("Проверка принадлежности карт одному пользователю");
        if (!fromCard.getUser().getId().equals(toCard.getUser().getId())) {
            logger.error("Переводы можно совершать только на свою карту. ID пользователя отправителя: {}, ID пользователя получателя: {}",
                    fromCard.getUser().getId(), toCard.getUser().getId());
            throw new IllegalArgumentException("Переводы можно совершать только на свою карту");
        }

        logger.debug("Проверка принадлежности карт указанному пользователю");
        if (!fromCard.getUser().getId().equals(transferDTO.getUserId()) || !toCard.getUser().getId().equals(transferDTO.getUserId())) {
            logger.error("Карты должны принадлежать указанному пользователю. ID пользователя: {}, ID пользователя карты отправителя: {}, ID пользователя карты получателя: {}",
                    transferDTO.getUserId(), fromCard.getUser().getId(), toCard.getUser().getId());
            throw new IllegalArgumentException("Карты должны принадлежать указанному пользователю");
        }

        logger.debug("Проверка статуса карт");
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            logger.error("Одна из карт заблокирована. Статус карты отправителя: {}, Статус карты получателя: {}",
                    fromCard.getStatus(), toCard.getStatus());
            throw new IllegalArgumentException("Карта заблокирована, обратитесь в банк");
        }

        logger.debug("Проверка достаточности баланса");
        if (fromCard.getBalance().compareTo(transferDTO.getAmount()) < 0) {
            logger.error("Недостаточно средств на карте отправителя. Баланс: {}, Сумма перевода: {}",
                    fromCard.getBalance(), transferDTO.getAmount());
            throw new IllegalArgumentException("Сумма перевода не должна превышать текущий баланс");
        }

        logger.debug("Обновление баланса карты отправителя. Текущий баланс: {}, Сумма перевода: {}", fromCard.getBalance(), transferDTO.getAmount());
        fromCard.setBalance(fromCard.getBalance().subtract(transferDTO.getAmount()));
        logger.debug("Обновление баланса карты получателя. Текущий баланс: {}, Сумма перевода: {}", toCard.getBalance(), transferDTO.getAmount());
        toCard.setBalance(toCard.getBalance().add(transferDTO.getAmount()));

        logger.debug("Сохранение изменений карт в базе данных");
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        logger.info("Балансы карт обновлены. Новый баланс карты отправителя: {}, Новый баланс карты получателя: {}",
                fromCard.getBalance(), toCard.getBalance());

        logger.debug("Создание сущности перевода");
        TransferEntity transferEntity = transferMapper.toEntity(transferDTO);
        transferEntity.setFromCard(fromCard);
        transferEntity.setToCard(toCard);
        transferEntity.setTransferTime(LocalDateTime.now());
        logger.debug("Сохранение перевода в базе данных");
        transferRepository.save(transferEntity);
        logger.info("Перевод успешно сохранен: {}", transferEntity);

        TransferEntityDTO result = transferMapper.toEntityDto(transferEntity);
        logger.info("Операция перевода завершена успешно. Результат: {}", result);
        return result;
    }
}