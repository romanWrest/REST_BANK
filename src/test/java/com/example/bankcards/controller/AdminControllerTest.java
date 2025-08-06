package com.example.bankcards.controller;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Card.CardResponseRequestStatusDTO;
import com.example.bankcards.dto.Card.CardSetStatusResponseDTO;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void setStatusCard_ShouldReturnOk() {
        Long cardId = 1L;
        CardStatus newStatus = CardStatus.BLOCK;
        CardSetStatusResponseDTO responseDTO = new CardSetStatusResponseDTO();
        responseDTO.setId(cardId);
        responseDTO.setStatus(newStatus);

        when(cardService.setStatusCard(cardId, newStatus)).thenReturn(responseDTO);

        ResponseEntity<CardSetStatusResponseDTO> response = adminController.setStatusCard(cardId, newStatus);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(cardService, times(1)).setStatusCard(cardId, newStatus);
    }

    @Test
    void getAllCards_ShouldReturnPageOfCards() {
        Pageable pageable = Pageable.ofSize(10);
        CardDTO cardDTO = createTestCardDTO();
        Page<CardDTO> page = new PageImpl<>(Collections.singletonList(cardDTO));

        when(cardService.getAllCards(pageable)).thenReturn(page);

        Page<CardDTO> result = adminController.getAllCards(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(cardDTO, result.getContent().get(0));
        verify(cardService, times(1)).getAllCards(pageable);
    }

    @Test
    void getStatusByRequestStatus_ShouldReturnPageOfStatuses() {
        Pageable pageable = Pageable.ofSize(10);
        CardResponseRequestStatusDTO statusDTO = new CardResponseRequestStatusDTO();
        Page<CardResponseRequestStatusDTO> page = new PageImpl<>(Collections.singletonList(statusDTO));

        when(cardService.getStatusesByRequestCards(pageable)).thenReturn(page);

        ResponseEntity<Page<CardResponseRequestStatusDTO>> response =
                adminController.getStatusByRequestStatus(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(statusDTO, response.getBody().getContent().get(0));
        verify(cardService, times(1)).getStatusesByRequestCards(pageable);
    }

    @Test
    void deleteCard_ShouldReturnNoContent() {
        Long cardId = 1L;
        doNothing().when(cardService).deleteCard(cardId);

        // Act
        ResponseEntity<Void> response = adminController.deleteCard(cardId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cardService, times(1)).deleteCard(cardId);
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        UserDTO userDTO = new UserDTO();
        Page<UserDTO> page = new PageImpl<>(Collections.singletonList(userDTO));

        when(userService.getAllUsers(pageable)).thenReturn(page);

        // Act
        ResponseEntity<Page<UserDTO>> response = adminController.getAllUsers(pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(userDTO, response.getBody().getContent().get(0));
        verify(userService, times(1)).getAllUsers(pageable);
    }

    @Test
    void getUserCards_ShouldReturnListOfCards() {
        // Arrange
        Long userId = 1L;
        CardDTO cardDTO = createTestCardDTO();
        List<CardDTO> cards = Collections.singletonList(cardDTO);

        when(userService.getUserCards(userId)).thenReturn(cards);

        // Act
        ResponseEntity<List<CardDTO>> response = adminController.getUserCards(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(cardDTO, response.getBody().get(0));
        verify(userService, times(1)).getUserCards(userId);
    }

    private CardDTO createTestCardDTO() {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(1L);
        cardDTO.setMaskedNumber("**** **** **** 1234");
        cardDTO.setBalance(BigDecimal.valueOf(1000));
        cardDTO.setExpiryDate(LocalDate.now().plusYears(1));
        cardDTO.setStatus(CardStatus.ACTIVE);
        return cardDTO;
    }
}