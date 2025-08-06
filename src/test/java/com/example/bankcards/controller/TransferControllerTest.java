package com.example.bankcards.controller;


import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferResponseDTO;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.transfer.TransferException;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private TransferDTO transferDTO;
    private TransferResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        transferDTO = new TransferDTO()
                .setUserId(1L)
                .setFromCardId(1L)
                .setToCardId(2L)
                .setAmount(new BigDecimal("100.00"));

        responseDTO = new TransferResponseDTO()
                .setId(1L)
                .setFromCard(1L)
                .setToCard(2L)
                .setAmount(new BigDecimal("100.00"))
                .setTransferTime(java.time.LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void transfer_Success() throws Exception {
        when(transferService.transfer(any(TransferDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fromCard").value(1L))
                .andExpect(jsonPath("$.toCard").value(2L))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void transfer_InvalidRequestData() throws Exception {
        TransferDTO invalidDTO = new TransferDTO();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void transfer_CardNotFound() throws Exception {
        when(transferService.transfer(any(TransferDTO.class)))
                .thenThrow(new CardNotFoundException("Card not found", 404));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void transfer_WhenCardBlocked_ShouldReturnBadRequest() throws Exception {
        when(transferService.transfer(any(TransferDTO.class)))
                .thenThrow(new TransferException("Card is blocked", 400));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isBadRequest());
    }
}