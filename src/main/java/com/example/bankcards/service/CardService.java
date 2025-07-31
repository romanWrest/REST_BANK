package com.example.bankcards.service;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Card.CardCreateDTO;
import com.example.bankcards.dto.Card.CardUpdateDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.DuplicateResourceException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.util.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final CardMapper cardMapper;

    @Transactional
    public CardDTO createCard(CardCreateDTO dto) {
        // Проверка уникальности номера карты
        if (cardRepository.findByNumber(dto.getNumber()).isPresent()) {
            throw new DuplicateResourceException("Card number already exists");
        }

        // Проверка существования пользователя
        UserEntity userEntity = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Проверка прав: только админ
        checkAdminAccess();

        CardEntity cardEntity = new CardEntity();
        cardEntity.setNumber(encryptionService.encrypt(dto.getNumber()));
        cardEntity.setOwner(dto.getOwner());
        cardEntity.setExpiryDate(dto.getExpiryDate());
        cardEntity.setBalance(dto.getBalance());
        cardEntity.setStatus(CardStatus.ACTIVE);
        cardEntity.setBank(dto.getBank());
        cardEntity.setUser(userEntity);
        cardEntity = cardRepository.save(cardEntity);
        return cardMapper.toCardDTO(cardEntity);
    }

    public CardDTO getCard(Long id) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // Проверка доступа: пользователь или админ
        checkUserAccess(cardEntity.getUser().getEmail());

        return cardMapper.toCardDTO(cardEntity);
    }

    /*public Page<CardDTO> getUserCards(Long userId, Pageable pageable) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        checkUserAccess(userEntity.getEmail());

        return cardRepository.findByUserId(userId, pageable).map(CardMapper.toCardDTO());
    }*/

    /*public Page<CardDTO> getUserCardsByStatus(Long userId, CardStatus status, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        // Проверка существования пользователя
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Проверка доступа
        checkUserAccess(userEntity.getEmail());

        return cardRepository.findByUserIdAndStatus(userId, status, pageable).map(CardMapper::toCardDTO);
    }*/

    @Transactional
    public CardDTO updateCard(Long id, CardUpdateDTO dto) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // Проверка прав: только админ
        checkAdminAccess();

        cardEntity.setStatus(dto.getStatus());
        if (dto.getExpiryDate() != null) {
            cardEntity.setExpiryDate(dto.getExpiryDate());
        }
        cardEntity = cardRepository.save(cardEntity);
        return cardMapper.toCardDTO(cardEntity);
    }

    @Transactional
    public void deleteCard(Long id) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // Проверка прав: только админ
        checkAdminAccess();

        cardRepository.delete(cardEntity);
    }

    private void checkUserAccess(String cardOwnerUsername) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !auth.getName().equals(cardOwnerUsername)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private void checkAdminAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
