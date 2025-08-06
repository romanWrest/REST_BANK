package com.example.bankcards.schedulers;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class CardStatusUpdateTask {
    private final CardRepository cardRepository;

    public CardStatusUpdateTask(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateExpiredCards() {
        log.info("Начало задачи обновления статуса карт с истекшим сроком действия");
        try {
            List<CardEntity> cards = cardRepository.findByStatusNotAndExpiryDateBefore(CardStatus.EXPIRED, LocalDate.now());
            if (cards.isEmpty()) {
                log.info("Карты с истекшим сроком действия не найдены");
                return;
            }
            for (CardEntity card : cards) {
                log.debug("Карта ID: {} имеет истекший срок действия: {}. Обновление статуса на EXPIRED",
                        card.getId(), card.getExpiryDate());
                card.setStatus(CardStatus.EXPIRED);
                cardRepository.save(card);
            }
            cardRepository.flush();
            log.info("Обновлено {} карт с истекшим сроком действия", cards.size());
        } catch (Exception e) {
            log.error("Ошибка при обновлении статуса карт: {}", e.getMessage(), e);
            throw new IllegalStateException("Не удалось обновить статусы карт", e);
        }
    }
}