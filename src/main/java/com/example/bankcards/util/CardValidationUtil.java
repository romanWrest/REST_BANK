package com.example.bankcards.util;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.transfer.TransferException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CardValidationUtil {
    public static void validateSameUserTransfer(CardEntity fromCard, CardEntity toCard) {
        log.debug("Проверка принадлежности карт одному пользователю");
        if (!fromCard.getUser().getId().equals(toCard.getUser().getId())) {
            log.error("Переводы можно совершать только на свою карту. ID пользователя отправителя: {}, ID пользователя получателя: {}",
                    fromCard.getUser().getId(), toCard.getUser().getId());
            throw new TransferException("Переводы можно совершать только на свою карту", 400);
        }
    }


    public static void validateCardsBelongToUser(TransferDTO transferDTO, CardEntity fromCard, CardEntity toCard) {
        log.debug("Проверка принадлежности карт текущему пользователю");
        if (!fromCard.getUser().getId().equals(transferDTO.getUserId()) || !toCard.getUser().getId().equals(transferDTO.getUserId())) {
            log.error("Карты должны принадлежать текущему пользователю. ID пользователя: {}, ID пользователя карты отправителя: {}, ID пользователя карты получателя: {}",
                    transferDTO.getUserId(), fromCard.getUser().getId(), toCard.getUser().getId());
            throw new TransferException("Карты должны принадлежать текущему пользователю", 403);
        }
    }


    public static void validateStatusesCards(CardEntity fromCard, CardEntity toCard) {
        log.debug("Проверка статуса карт");
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            log.error("Одна из карт заблокирована. Статус карты отправителя: {}, Статус карты получателя: {}",
                    fromCard.getStatus(), toCard.getStatus());
            throw new TransferException("Недостаточно средств или карта заблокирована(BLOCK, EXPIRED)", 422);
        }
    }


    public static void validateBalanceCard(TransferDTO transferDTO, CardEntity fromCard, CardEntity toCard) {
        log.debug("Проверка достаточности баланса");
        if (fromCard.getBalance().

                compareTo(transferDTO.getAmount()) < 0) {
            log.error("Недостаточно средств на карте отправителя. Баланс: {}, Сумма перевода: {}",
                    fromCard.getBalance(), transferDTO.getAmount());
            throw new TransferException("Недостаточно средств или карта заблокирована(BLOCK, EXPIRED)", 400);
        }
    }
}
