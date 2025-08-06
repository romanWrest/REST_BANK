package com.example.bankcards.service;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferResponseDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.TransferEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.util.CardValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    private CardEntity fromCard;
    private CardEntity toCard;
    private TransferDTO transferDTO;
    private TransferEntity transferEntity;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        fromCard = new CardEntity();
        fromCard.setId(3L);
        fromCard.setId(3L);
        fromCard.setBalance(new BigDecimal("1000.00"));
        fromCard.setStatus(CardStatus.ACTIVE);

        toCard = new CardEntity();
        toCard.setId(2L);
        toCard.setId(1L);
        toCard.setBalance(new BigDecimal("500.00"));
        toCard.setStatus(CardStatus.ACTIVE);

        transferDTO = new TransferDTO();
        transferDTO.setUserId(1L);
        transferDTO.setFromCardId(1L);
        transferDTO.setToCardId(2L);
        transferDTO.setAmount(new BigDecimal("100.00"));

        transferEntity = new TransferEntity();
        transferEntity.setId(1L);
        transferEntity.setFromCard(fromCard);
        transferEntity.setToCard(toCard);
        transferEntity.setAmount(new BigDecimal("100.00"));
        transferEntity.setTransferTime(LocalDateTime.now());
    }

    @Test
    void testTransfer_Successful() {

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(transferRepository.save(any(TransferEntity.class))).thenReturn(transferEntity);

        try (MockedStatic<CardValidationUtil> mocked = mockStatic(CardValidationUtil.class)) {
            mocked.when(() -> CardValidationUtil.validateSameUserTransfer(any(), any())).thenAnswer(invocation -> null);
            mocked.when(() -> CardValidationUtil.validateCardsBelongToUser(any(), any(), any())).thenAnswer(invocation -> null);
            mocked.when(() -> CardValidationUtil.validateStatusesCards(any(), any())).thenAnswer(invocation -> null);
            mocked.when(() -> CardValidationUtil.validateBalanceCard(any(), any(), any())).thenAnswer(invocation -> null);


            TransferResponseDTO result = transferService.transfer(transferDTO);


            assertNotNull(result);
            assertEquals(transferDTO.getFromCardId(), result.getFromCard());
            assertEquals(transferDTO.getToCardId(), result.getToCard());
            assertEquals(transferDTO.getAmount(), result.getAmount());
            assertEquals(new BigDecimal("900.00"), fromCard.getBalance());
            assertEquals(new BigDecimal("600.00"), toCard.getBalance());
            verify(cardRepository, times(2)).save(any(CardEntity.class));
            verify(transferRepository, times(1)).save(any(TransferEntity.class));
        }
    }

    @Test
    void testTransfer_FromCardNotFound_ThrowsException() {

        when(cardRepository.findById(1L)).thenReturn(Optional.empty());


        CardNotFoundException exception = assertThrows(CardNotFoundException.class, () -> transferService.transfer(transferDTO));
        assertEquals("Карта не найдена с ID: 1", exception.getMessage());
        assertEquals(404, exception.getStatus());
        verify(cardRepository, never()).save(any(CardEntity.class));
        verify(transferRepository, never()).save(any(TransferEntity.class));
    }

    @Test
    void testTransfer_ToCardNotFound_ThrowsException() {

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.empty());


        CardNotFoundException exception = assertThrows(CardNotFoundException.class, () -> transferService.transfer(transferDTO));
        assertEquals("Карта не найдена с ID: 2", exception.getMessage());
        assertEquals(404, exception.getStatus());
        verify(cardRepository, never()).save(any(CardEntity.class));
        verify(transferRepository, never()).save(any(TransferEntity.class));
    }

    @Test
    void testTransfer_InvalidBalance_ThrowsException() {

        transferDTO.setAmount(new BigDecimal("2000.00")); // More than fromCard balance
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        try (MockedStatic<CardValidationUtil> mocked = mockStatic(CardValidationUtil.class)) {
            mocked.when(() -> CardValidationUtil.validateSameUserTransfer(any(), any())).thenAnswer(invocation -> null);
            mocked.when(() -> CardValidationUtil.validateCardsBelongToUser(any(), any(), any())).thenAnswer(invocation -> null);
            mocked.when(() -> CardValidationUtil.validateStatusesCards(any(), any())).thenAnswer(invocation -> null);
            mocked.when(() -> CardValidationUtil.validateBalanceCard(any(), any(), any()))
                    .thenThrow(new IllegalArgumentException("Недостаточно средств на карте"));


            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transferService.transfer(transferDTO));
            assertEquals("Недостаточно средств на карте", exception.getMessage());
            verify(cardRepository, never()).save(any(CardEntity.class));
            verify(transferRepository, never()).save(any(TransferEntity.class));
        }
    }
}
