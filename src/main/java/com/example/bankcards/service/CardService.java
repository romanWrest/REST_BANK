package com.example.bankcards.service;

import com.example.bankcards.dto.Card.*;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.DuplicateResourceException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @Transactional
    public CardDTO createCard(CardCreateDTO dto) {
        log.debug("Попытка создания карты с номером: {} для пользователя с ID: {}", dto.getNumber(), dto.getUserId());

        if (cardRepository.findByNumber(dto.getNumber()).isPresent()) {
            log.error("Ошибка создания карты: карта с номером {} уже существует", dto.getNumber());
            throw new DuplicateResourceException("Карта с таким номером уже существует");
        }

        UserEntity userEntity = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error("Пользователь с ID: {} не найден", dto.getUserId());
                    return new UserNotFoundException("Пользователь не найден", 404);
                });

        CardEntity cardEntity = new CardEntity();
        cardEntity.setNumber(dto.getNumber());
        cardEntity.setBalance(dto.getBalance());
        cardEntity.setStatus(CardStatus.ACTIVE);
        cardEntity.setUser(userEntity);
        cardEntity.setExpiryDate(LocalDate.now().plusYears(1));
        cardEntity = cardRepository.save(cardEntity);

        log.info("Карта успешно создана с ID: {} для пользователя с ID: {}", cardEntity.getId(), dto.getUserId());
        return cardMapper.toCardDTO(cardEntity);
    }

    public CardDTO getCard(Long id) {
        log.debug("Получение карты с ID: {}", id);
        CardEntity cardEntity = getCardById(id);
        log.info("Карта с ID: {} успешно получена", id);
        return cardMapper.toCardDTO(cardEntity);
    }

    @Transactional(readOnly = true)
    public Page<CardDTO> getUserCards(Long userId, Pageable pageable) {
        log.debug("Получение карт для пользователя с ID: {} с пагинацией: {}", userId, pageable);

        if (pageable == null) {
            log.error("Pageable равен null для пользователя с ID: {}", userId);
            throw new IllegalArgumentException("Pageable не может быть null");
        }
        int maxPageSize = 100;
        if (pageable.getPageSize() > maxPageSize) {
            log.warn("Запрошенный размер страницы {} превышает максимальный лимит {} для пользователя с ID: {}",
                    pageable.getPageSize(), maxPageSize, userId);
            throw new IllegalArgumentException("Размер страницы не должен превышать " + maxPageSize);
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID: {} не найден", userId);
                    return new UserNotFoundException("Пользователь не найден", 404);
                });

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || (!isAdmin(auth) && !userEntity.getEmail().equals(auth.getName()))) {
            log.warn("Доступ запрещен к картам пользователя с ID: {} для пользователя: {}",
                    userId, auth != null ? auth.getName() : "анонимный");
            throw new AccessDeniedException("Доступ запрещен");
        }

        Page<CardDTO> result = cardRepository.findByUserId(userId, pageable)
                .map(cardMapper::toCardDTO);
        log.info("Успешно получено {} карт для пользователя с ID: {}", result.getTotalElements(), userId);
        return result;
    }

    public static boolean isAdmin(Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        log.debug("Проверка прав администратора для пользователя: {}. Является администратором: {}",
                auth != null ? auth.getName() : "анонимный", isAdmin);
        return isAdmin;
    }

    @Transactional
    public CardSetStatusResponseDTO setStatusCard(Long id, CardStatus status) {
        log.debug("Попытка установки статуса для карты с ID: {} на статус: {}", id, status);
        CardEntity cardEntity = getCardById(id);
        cardEntity.setStatus(status);
        cardEntity.setBlockRequestStatus(BlockRequestStatus.APPROVED);
        cardEntity = cardRepository.save(cardEntity);

        log.info("Статус карты обновлен для ID: {}. Новый статус: {}, Статус запроса блокировки: {}",
                cardEntity.getId(), cardEntity.getStatus(), cardEntity.getBlockRequestStatus());

        CardSetStatusResponseDTO responseDTO = new CardSetStatusResponseDTO();
        responseDTO.setId(cardEntity.getId());
        responseDTO.setStatus(cardEntity.getStatus());
        responseDTO.setBlockRequestStatus(cardEntity.getBlockRequestStatus());
        return responseDTO;
    }

    @Transactional(readOnly = true)
    public CardResponseBalanceDTO getBalanceCardByCardId(Long id) {
        log.debug("Получение баланса для карты с ID: {}", id);
        CardEntity cardEntity = getCardById(id);
        log.info("Баланс успешно получен для карты с ID: {}. Баланс: {}", id, cardEntity.getBalance());

        CardResponseBalanceDTO responseDTO = new CardResponseBalanceDTO();
        responseDTO.setBalance(cardEntity.getBalance());
        responseDTO.setId(cardEntity.getId());
        return responseDTO;
    }

    @Transactional
    public CardResponseBlockDTO requestBlock(Long cardId) {
        log.debug("Запрос блокировки для карты с ID: {}", cardId);
        CardEntity cardEntity = getCardById(cardId);
        cardEntity.setBlockRequestStatus(BlockRequestStatus.PENDING);
        cardEntity = cardRepository.save(cardEntity);

        log.info("Запрос блокировки инициирован для карты с ID: {}. Статус: {}",
                cardId, cardEntity.getBlockRequestStatus());

        return new CardResponseBlockDTO(cardEntity.getId(), cardEntity.getBlockRequestStatus());
    }

    @Transactional
    public void deleteCard(Long id) {
        log.debug("Попытка удаления карты с ID: {}", id);
        CardEntity cardEntity = getCardById(id);
        cardRepository.delete(cardEntity);
        log.info("Карта с ID: {} успешно удалена", id);
    }

    @Transactional(readOnly = true)
    public Page<CardDTO> getAllCards(Pageable pageable) {
        log.debug("Получение всех карт с пагинацией: {}", pageable);

        Page<CardEntity> cardPage = cardRepository.findAll(pageable);
        List<CardDTO> result = cardPage.getContent()
                .stream()
                .map(cardMapper::toCardDTO)
                .collect(Collectors.toList());

        log.info("Успешно получено {} карт", cardPage.getTotalPages());
        return new PageImpl<>(result, pageable, cardPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<CardResponseRequestStatusDTO> getStatusesByRequestCards(Pageable pageable) {
        log.debug("Получение статусов карт с пагинацией: {}", pageable);

        if (pageable == null) {
            log.error("Pageable равен null для getStatusesByRequestCards");
            throw new IllegalArgumentException("Pageable не может быть null");
        }

        Page<CardResponseRequestStatusDTO> cardPage = cardRepository.findAll(pageable)
                .map(cardMapper::toCardResponseRequestStatusDTO);

        log.info("Успешно получено {} статусов карт", cardPage.getTotalElements());
        return cardPage;
    }

    private CardEntity getCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Карта с ID: {} не найдена", id);
                    return new CardNotFoundException("Карта с id " + id + " не найдена", 404);
                });
    }
}