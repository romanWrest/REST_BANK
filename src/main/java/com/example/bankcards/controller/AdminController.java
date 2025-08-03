package com.example.bankcards.controller;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Card.CardResponseRequestStatusDTO;
import com.example.bankcards.dto.Card.CardSetStatusDTO;
import com.example.bankcards.dto.Card.CardSetStatusResponseDTO;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final UserService userService;

    @PatchMapping("/status")
    public ResponseEntity<CardSetStatusResponseDTO> setStatusCard(@Valid @RequestBody CardSetStatusDTO cardSetStatusDTO) {
        CardSetStatusResponseDTO cardSetStatusResponseDTO = cardService.setStatusCard(cardSetStatusDTO);
        return new ResponseEntity<>(cardSetStatusResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/cards")
    public List<CardDTO> getAllCards(Pageable pageable) {
        return cardService.getAllCards(pageable);
    }

    @GetMapping("{id}/cards")
    public ResponseEntity<Page<CardResponseRequestStatusDTO>> getStatusByRequestStatus(@Valid @NotNull Pageable pageable) {
        Page<CardResponseRequestStatusDTO> dto = cardService.getStatusesByRequestCards(pageable);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable("id") Long id) {
        cardService.deleteCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllUsers(@Valid @NotNull Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // еще метод для всей инфы о юзере столбец с БД просто на выход
    @GetMapping("/info")
    public ResponseEntity<List<CardDTO>> getUserCards(@PathVariable("id") Long id) {
        List<CardDTO> cards = userService.getUserCards(id);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
