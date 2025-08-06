package com.example.bankcards.controller;


import com.example.bankcards.dto.Card.*;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private UserService userService;


    @Autowired
    private ObjectMapper objectMapper;


    private CardDTO testCardDTO;
    private CardCreateDTO testCardCreateDTO;
    private CardResponseBalanceDTO testBalanceDTO;
    private CardResponseBlockDTO testBlockResponseDTO;

    @BeforeEach
    void setUp() {
        testCardDTO = new CardDTO()
                .setId(1L)
                .setMaskedNumber("**** **** **** 1234")
                .setBalance(BigDecimal.valueOf(1000))
                .setExpiryDate(LocalDate.now().plusYears(1));

        testCardCreateDTO = new CardCreateDTO()
                .setNumber("1111 2222 3333 1234")
                .setStatus(CardStatus.ACTIVE)
                .setBalance(BigDecimal.valueOf(1000))
                .setUserId(1L);

        testBalanceDTO = new CardResponseBalanceDTO()
                .setBalance(BigDecimal.valueOf(1000))
                .setId(1L);

        testBlockResponseDTO = new CardResponseBlockDTO()
                .setId(1L)
                .setBlockRequestStatus(BlockRequestStatus.REJECTED);
    }

    @Test
    void createCard_ShouldReturnCreatedStatus() throws Exception {
        when(cardService.createCard(any(CardCreateDTO.class))).thenReturn(testCardDTO);

        mockMvc.perform(post("/api/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCardCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void getCard_ShouldReturnCard() throws Exception {
        when(cardService.getCard(1L)).thenReturn(testCardDTO);

        mockMvc.perform(get("/api/cards/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getUserCards_ShouldReturnPaginatedCards() throws Exception {
        Page<CardDTO> page = new PageImpl<>(List.of(testCardDTO), PageRequest.of(0, 10), 1);
        when(cardService.getUserCards(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/cards/{id}/userCards", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getBalance_ShouldReturnBalance() throws Exception {
        when(cardService.getBalanceCardByCardId(1L)).thenReturn(testBalanceDTO);

        mockMvc.perform(get("/api/cards/{id}/balance", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void requestToBlockCard_ShouldReturnSuccessResponse() throws Exception {
        when(cardService.requestBlock(1L)).thenReturn(testBlockResponseDTO);

        mockMvc.perform(patch("/api/cards/block/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setStatusCard_ShouldReturnOk() throws Exception {
        Long cardId = 1L;
        CardStatus status = CardStatus.BLOCK;
        CardSetStatusResponseDTO responseDTO = new CardSetStatusResponseDTO()
                .setId(cardId)
                .setStatus(status);

        given(cardService.setStatusCard(eq(cardId), eq(status))).willReturn(responseDTO);

        mockMvc.perform(patch("/api/cards/status")
                        .param("id", cardId.toString())
                        .param("status", status.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId))
                .andExpect(jsonPath("$.status").value(status.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCards_ShouldReturnPageOfCards() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        CardDTO cardDTO = new CardDTO()
                .setId(1L)
                .setMaskedNumber("**** **** **** 3456")
                .setStatus(CardStatus.ACTIVE)
                .setExpiryDate(LocalDate.now().plusYears(3));
        Page<CardDTO> page = new PageImpl<>(Collections.singletonList(cardDTO), pageable, 1);

        given(cardService.getAllCards(any(Pageable.class))).willReturn(page);


        mockMvc.perform(get("/api/cards/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cardDTO.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStatusByRequestStatus_ShouldReturnPageOfStatuses() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);
        CardResponseRequestStatusDTO statusDTO = new CardResponseRequestStatusDTO()
                .setId(1L)
                .setStatus(CardStatus.ACTIVE);
        Page<CardResponseRequestStatusDTO> page = new PageImpl<>(Collections.singletonList(statusDTO), pageable, 1);

        given(cardService.getStatusesByRequestCards(any(Pageable.class))).willReturn(page);


        mockMvc.perform(get("/api/cards/statuses/request/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(statusDTO.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_ShouldReturnNoContent() throws Exception {

        Long cardId = 1L;
        doNothing().when(cardService).deleteCard(cardId);


        mockMvc.perform(delete("/api/cards/delete/{id}", cardId))
                .andExpect(status().isNoContent());
    }
}