package com.example.bankcards.controller;

import com.example.bankcards.dto.Card.CardCreateDTO;
import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Card.CardResponseBalanceDTO;
import com.example.bankcards.dto.Card.CardResponseBlockDTO;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private CardDTO testCardDTO;
    private CardCreateDTO testCardCreateDTO;
    private CardResponseBalanceDTO testBalanceDTO;
    private CardResponseBlockDTO testBlockResponseDTO;

    @BeforeEach
    void setUp() {
        testCardDTO = new CardDTO();
        testCardDTO.setId(1L);
        testCardDTO.setMaskedNumber("**** **** **** 1234");
        testCardDTO.setBalance(BigDecimal.valueOf(1000));
        testCardDTO.setExpiryDate(LocalDate.now().plusYears(1));

        testCardCreateDTO = new CardCreateDTO();
        // установите поля для testCardCreateDTO

        testBalanceDTO = new CardResponseBalanceDTO();
        testBalanceDTO.setId(1L);
        testBalanceDTO.setBalance(BigDecimal.valueOf(1000));

        testBlockResponseDTO = new CardResponseBlockDTO();
        // установите поля для testBlockResponseDTO
    }

    @Test
    void createCard_ShouldReturnCreatedStatus() {
        // Arrange
        when(cardService.createCard(any(CardCreateDTO.class))).thenReturn(testCardDTO);

        // Act
        ResponseEntity<CardDTO> response = cardController.createCard(testCardCreateDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testCardDTO, response.getBody());
        verify(cardService, times(1)).createCard(testCardCreateDTO);
    }

    @Test
    void getCard_ShouldReturnOkStatus() {
        // Arrange
        Long cardId = 1L;
        when(cardService.getCard(cardId)).thenReturn(testCardDTO);

        // Act
        ResponseEntity<CardDTO> response = cardController.getCard(cardId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCardDTO, response.getBody());
        verify(cardService, times(1)).getCard(cardId);
    }

    @Test
    void getUserCards_ShouldReturnPageOfCards() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = Pageable.ofSize(10);
        Page<CardDTO> page = new PageImpl<>(Collections.singletonList(testCardDTO));
        when(cardService.getUserCards(userId, pageable)).thenReturn(page);

        // Act
        ResponseEntity<Page<CardDTO>> response = cardController.getUserCards(userId, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(testCardDTO, response.getBody().getContent().get(0));
        verify(cardService, times(1)).getUserCards(userId, pageable);
    }

    @Test
    void getBalance_ShouldReturnBalance() {
        // Arrange
        Long cardId = 1L;
        when(cardService.getBalanceCardByCardId(cardId)).thenReturn(testBalanceDTO);

        // Act
        ResponseEntity<CardResponseBalanceDTO> response = cardController.getBalance(cardId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testBalanceDTO, response.getBody());
        verify(cardService, times(1)).getBalanceCardByCardId(cardId);
    }

    @Test
    void requestToBlockCard_ShouldReturnBlockResponse() {
        // Arrange
        Long cardId = 1L;
        when(cardService.requestBlock(cardId)).thenReturn(testBlockResponseDTO);

        // Act
        ResponseEntity<CardResponseBlockDTO> response = cardController.requestToBlockCard(cardId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testBlockResponseDTO, response.getBody());
        verify(cardService, times(1)).requestBlock(cardId);
    }
}