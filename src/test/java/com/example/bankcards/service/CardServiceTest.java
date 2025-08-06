package com.example.bankcards.service;

import com.example.bankcards.dto.Card.*;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.RoleUsers;
import com.example.bankcards.exception.DuplicateResourceException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Импорты остаются теми же...

@ExtendWith(MockitoExtension.class)
class CardServiceTest {


    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private CardService cardService;

    private CardEntity testCardEntity;
    private UserEntity testUserEntity;
    private CardDTO testCardDTO;
    private CardCreateDTO testCardCreateDTO;
    private CardResponseBalanceDTO testBalanceDTO;
    private CardResponseBlockDTO testBlockResponseDTO;
    private CardSetStatusResponseDTO testStatusResponseDTO;
    private CardResponseRequestStatusDTO testRequestStatusDTO;

    @BeforeEach
    void setUp() {
        testUserEntity = new UserEntity();
        testUserEntity.setId(1L);
        testUserEntity.setEmail("user@example.com");
        testUserEntity.setRole(RoleUsers.ROLE_USER);

        testCardEntity = new CardEntity();
        testCardEntity.setId(1L);
        testCardEntity.setNumber("1234123412341234");
        testCardEntity.setBalance(BigDecimal.valueOf(1000));
        testCardEntity.setStatus(CardStatus.ACTIVE);
        testCardEntity.setExpiryDate(LocalDate.now().plusYears(1));
        testCardEntity.setUser(testUserEntity);

        testCardDTO = new CardDTO();
        testCardDTO.setId(1L);
        testCardDTO.setMaskedNumber("**** **** **** 1234");
        testCardDTO.setBalance(BigDecimal.valueOf(1000));
        testCardDTO.setStatus(CardStatus.ACTIVE);
        testCardDTO.setExpiryDate(LocalDate.now().plusYears(1));

        testCardCreateDTO = new CardCreateDTO();
        testCardCreateDTO.setNumber("1234123412341234");
        testCardCreateDTO.setBalance(BigDecimal.valueOf(1000));
        testCardCreateDTO.setUserId(1L);

        testBalanceDTO = new CardResponseBalanceDTO();
        testBalanceDTO.setId(1L);
        testBalanceDTO.setBalance(BigDecimal.valueOf(1000));

        testBlockResponseDTO = new CardResponseBlockDTO(1L, BlockRequestStatus.PENDING);

        testStatusResponseDTO = new CardSetStatusResponseDTO();
        testStatusResponseDTO.setId(1L);
        testStatusResponseDTO.setStatus(CardStatus.BLOCK);
        testStatusResponseDTO.setBlockRequestStatus(BlockRequestStatus.APPROVED);

        testRequestStatusDTO = new CardResponseRequestStatusDTO();
        testRequestStatusDTO.setId(1L);
        testRequestStatusDTO.setMaskedNumber("**** **** **** 1234");
        testRequestStatusDTO.setUser(testUserEntity);
        testRequestStatusDTO.setBlockRequestStatus(BlockRequestStatus.PENDING);
        testRequestStatusDTO.setStatus(CardStatus.ACTIVE);
    }



    @Test
    void getAllCards_ShouldReturnAllCards() {
        // 1. Подготовка данных
        List<CardEntity> cardList = Collections.singletonList(testCardEntity);
        Page<CardEntity> cardPage = new PageImpl<>(cardList, pageable, cardList.size());

        // 2. Настройка моков
        when(cardRepository.findAll(any(Pageable.class))).thenReturn(cardPage);
        when(cardMapper.toCardDTO(any(CardEntity.class))).thenReturn(testCardDTO);

        // 3. Вызов метода
        Page<CardDTO> result = cardService.getAllCards(PageRequest.of(0, 10));

        // 4. Проверки
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testCardDTO, result.getContent().get(0));
    }

    @Test
    void getStatusesByRequestCards_ShouldReturnStatuses() {
        // 1. Подготовка данных
        List<CardEntity> cardList = Collections.singletonList(testCardEntity);
        Page<CardEntity> cardPage = new PageImpl<>(cardList, pageable, cardList.size());

        // 2. Настройка моков
        when(cardRepository.findAll(any(Pageable.class))).thenReturn(cardPage);
        when(cardMapper.toCardResponseRequestStatusDTO(any(CardEntity.class))).thenReturn(testRequestStatusDTO);

        // 3. Вызов метода
        Page<CardResponseRequestStatusDTO> result = cardService.getStatusesByRequestCards(PageRequest.of(0, 10));

        // 4. Проверки
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testRequestStatusDTO, result.getContent().get(0));
    }
}