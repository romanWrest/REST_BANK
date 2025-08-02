package com.example.bankcards.service;

import com.example.bankcards.dto.Card.*;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.DuplicateResourceException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardService cardService;

    @Transactional
    public CardDTO createCard(CardCreateDTO dto) {
        if (cardRepository.findByNumber(dto.getNumber()).isPresent()) {
            throw new DuplicateResourceException("Card number already exists");
        }

        UserEntity userEntity = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CardEntity cardEntity = new CardEntity();
        cardEntity.setNumber(dto.getNumber());
        cardEntity.setBalance(dto.getBalance());
        cardEntity.setStatus(CardStatus.ACTIVE);
        cardEntity.setUser(userEntity);
        cardEntity = cardRepository.save(cardEntity);
        return cardMapper.toCardDTO(cardEntity);
    }

    public CardDTO getCard(Long id) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        return cardMapper.toCardDTO(cardEntity);
    }

    @Transactional(readOnly = true)
    public Page<CardDTO> getUserCards(Long userId, Pageable pageable) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cardRepository.findByUserId(userId, pageable).map(cardMapper::toCardDTO);
    }

    @Transactional
    public CardSetStatusResponseDTO setStatusCard(CardSetStatusDTO cardSetStatusDTO) {
        CardEntity cardEntity = cardRepository.findById(cardSetStatusDTO.getId()).
                orElseThrow(() -> new RuntimeException("Card not found"));
        cardEntity.setStatus(cardSetStatusDTO.getStatus());
        cardEntity.setBlockRequestStatus(BlockRequestStatus.APPROVED);
        cardEntity = cardRepository.save(cardEntity);

        CardSetStatusResponseDTO responseDTO = new CardSetStatusResponseDTO();
        responseDTO.setId(cardEntity.getId());
        responseDTO.setStatus(cardEntity.getStatus());
        responseDTO.setBlockRequestStatus(cardEntity.getBlockRequestStatus());
        return responseDTO;
    }


    @Transactional(readOnly = true)
    public CardResponseBalanceDTO getBalanceCardByCardId(Long id) {
        CardEntity cardEntity = cardRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Card not found"));

        CardResponseBalanceDTO responseDTO = new CardResponseBalanceDTO();
        responseDTO.setBalance(cardEntity.getBalance());
        responseDTO.setId(cardEntity.getId());
        return responseDTO;
    }

    @Transactional
    public CardResponseBlockDTO requestBlock(Long cardId) {
        CardEntity cardEntity = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card with id " + cardId + " not found"));
        cardEntity.setBlockRequestStatus(BlockRequestStatus.PENDING);
        cardEntity = cardRepository.save(cardEntity);
        return new CardResponseBlockDTO(cardEntity.getId(), cardEntity.getBlockRequestStatus());
    }


    @Transactional
    public void deleteCard(Long id) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        cardRepository.delete(cardEntity);
    }

    @Transactional(readOnly = true)
    public List<CardDTO> getAllCards(Pageable pageable) {
        Page<CardEntity> cardPage = cardRepository.findAll(pageable);
        return cardPage.getContent()
                .stream()
                .map(cardMapper::toCardDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CardDTO> getCardsByUserId(Long userId, Pageable pageable) {
        Page<CardEntity> cardPage = cardRepository.findByUserId(userId, pageable);
        if (cardPage.isEmpty()) {
            throw new CardNotFoundException("No cards found for user with ID: " + userId);
        }
        return cardPage.getContent()
                .stream()
                .map(cardMapper::toCardDTO)
                .collect(Collectors.toList());
    }


}
