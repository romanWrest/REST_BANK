package com.example.bankcards.controller;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferResponseDTO;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.transfer.TransferException;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String VALID_JWT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Test
    @WithMockUser(username = "user", roles = "ROLE_USER")
    void transfer_Success() throws Exception {
        doNothing().when(jwtFilter).doFilter(any(), any(), any());

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("100.00"));

        TransferResponseDTO responseDTO = new TransferResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFromCard(1L);
        responseDTO.setToCard(2L);
        responseDTO.setAmount(new BigDecimal("100.00"));

        when(transferService.transfer(any(TransferDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .header("Authorization", VALID_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1L))
                .andExpect(jsonPath("$.fromCard").value(1L))
                .andExpect(jsonPath("$.toCard").value(2L))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void transfer_InvalidRequestData() throws Exception {
        // Arrange
        TransferDTO transferDTO = new TransferDTO();
        // Поля не заполнены, что вызовет ошибку валидации

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .header("Authorization", VALID_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "ROLE_USER")
    void transfer_CardNotFound() throws Exception {
        // Arrange
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("100.00"));

        when(transferService.transfer(any(TransferDTO.class)))
                .thenThrow(new CardNotFoundException("Card not found", 404));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .header("Authorization", VALID_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void transfer_Unauthorized() throws Exception {
        // Arrange
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("100.00"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "ROLE_USER")
    void transfer_InsufficientFunds() throws Exception {
        // Arrange
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("100.00"));

        when(transferService.transfer(any(TransferDTO.class)))
                .thenThrow(new TransferException("Insufficient funds or card blocked", 404));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/transfer")
                        .header("Authorization", VALID_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isBadRequest());
    }
}