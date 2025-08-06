package com.example.bankcards.controller;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Card.CardResponseRequestStatusDTO;
import com.example.bankcards.dto.Card.CardSetStatusResponseDTO;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin API", description = "API для администрирования банковских карт и пользователей")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final CardService cardService;
    private final UserService userService;

    @PatchMapping("/status")
    @Operation(summary = "Изменить статус карты", description = "Позволяет администратору изменить статус банковской карты (ACTIVE, BLOCK, EXPIRED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус карты успешно изменён"),
            @ApiResponse(responseCode = "400", description = "Неверные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    public ResponseEntity<CardSetStatusResponseDTO> setStatusCard(
            @RequestParam @NotNull Long id,
            @RequestParam @NotNull CardStatus status) {
        CardSetStatusResponseDTO cardSetStatusResponseDTO = cardService.setStatusCard(id, status);
        return new ResponseEntity<>(cardSetStatusResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/cards")
    @Operation(summary = "Получить все карты всех пользователей", description = "Возвращает список абсолютно всех банковских карт с поддержкой пагинации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public Page<CardDTO> getAllCards(
            @Parameter(description = "Параметры пагинации (page, size, sort)", required = true) Pageable pageable) {
        return cardService.getAllCards(pageable);
    }

    @GetMapping("/statuses/request/cards")
    @Operation(summary = "Получить статусы запросов на блокировку карт по запросам", description = "Возвращает список статусов запросов на блокировку карт по запросам с пагинацией.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список статусов успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден")
    })
    public ResponseEntity<Page<CardResponseRequestStatusDTO>> getStatusByRequestStatus(
            @Parameter(description = "Параметры пагинации (page, size, sort)", required = true) @Valid @NotNull Pageable pageable) {
        Page<CardResponseRequestStatusDTO> dto = cardService.getStatusesByRequestCards(pageable);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить карту", description = "Удаляет банковскую карту по её ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Карта успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "ID карты для удаления", required = true) @PathVariable("id") Long id) {
        cardService.deleteCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей с пагинацией.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @Parameter(description = "Параметры пагинации (page, size, sort)", required = true) @Valid @NotNull Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/{id}/cards")
    @Operation(summary = "Получить карты пользователя", description = "Возвращает список карт, принадлежащих пользователю по его ID. Доступно только для администратора")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт пользователя успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<List<CardDTO>> getUserCards(
            @Parameter(description = "ID пользователя", required = true) @PathVariable("id") Long id) {
        List<CardDTO> cards = userService.getUserCards(id);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}