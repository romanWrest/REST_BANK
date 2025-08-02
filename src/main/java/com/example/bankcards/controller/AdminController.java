package com.example.bankcards.controller;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Card.CardSetStatusDTO;
import com.example.bankcards.dto.Card.CardSetStatusResponseDTO;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final CardService cardService;

    @PatchMapping("/status")
    public ResponseEntity<CardSetStatusResponseDTO> setStatusCard(@Valid @RequestBody CardSetStatusDTO cardSetStatusDTO) {
        CardSetStatusResponseDTO cardSetStatusResponseDTO = cardService.setStatusCard(cardSetStatusDTO);
        return new ResponseEntity<>(cardSetStatusResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/cards")
    public List<CardDTO> getAllCards(Pageable pageable) {
        return cardService.getAllCards(pageable);
    }

    // еще метод для всей инфы о юзере столбец с БД просто на выход

}
