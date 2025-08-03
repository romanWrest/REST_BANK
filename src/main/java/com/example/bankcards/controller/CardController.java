package com.example.bankcards.controller;

import com.example.bankcards.dto.Card.CardCreateDTO;
import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Card.CardResponseBalanceDTO;
import com.example.bankcards.dto.Card.CardResponseBlockDTO;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardController {
    private static final Logger log = LogManager.getLogger(CardController.class);
    private final CardService cardService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardCreateDTO cardCreateDTO) {
        log.info("Received cardCreateDTO: {}", cardCreateDTO);
        CardDTO cardDTO = cardService.createCard(cardCreateDTO);
        return new ResponseEntity<>(cardDTO, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable("id") Long id) {
        CardDTO cardDTO = cardService.getCard(id);
        return new ResponseEntity<>(cardDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/userCards")
    public ResponseEntity<Page<CardDTO>> getUserCards(@PathVariable("id") Long id, @Valid @NotNull Pageable pageable) {
        Page<CardDTO> userCards = cardService.getUserCards(id, pageable);
        return new ResponseEntity<>(userCards, HttpStatus.OK);
    }


    @GetMapping("/{id}/balance")
    public ResponseEntity<CardResponseBalanceDTO> getBalance(@PathVariable("id") Long id) {
        CardResponseBalanceDTO cardResponseBalanceDTO = cardService.getBalanceCardByCardId(id);
        return new ResponseEntity<>(cardResponseBalanceDTO, HttpStatus.OK);
    }

    @PatchMapping("/block")
    public ResponseEntity<CardResponseBlockDTO> requestToBlockCard(@PathVariable("id") Long id) {
        log.info("Requesting block for card id: {}", id);
        CardResponseBlockDTO response = cardService.requestBlock(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




}
