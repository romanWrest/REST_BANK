package com.example.bankcards.controller;

import com.example.bankcards.dto.Card.*;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Card Management API", description = "API для управления банковскими картами пользователей")
@SecurityRequirement(name = "bearerAuth")
public class CardController {
    private static final Logger log = LogManager.getLogger(CardController.class);
    private final CardService cardService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Создать новую карту", description = "Создаёт новую банковскую карту для пользователя. Доступно только администратору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "409", description = "Карта с такими данными уже существует")
    })
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardCreateDTO cardCreateDTO) {
        log.info("Received cardCreateDTO: {}", cardCreateDTO);
        CardDTO cardDTO = cardService.createCard(cardCreateDTO);
        return new ResponseEntity<>(cardDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по ID", description = "Возвращает информацию о карте по её ID. Доступно для авторизованных пользователей.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    public ResponseEntity<CardDTO> getCard(
            @Parameter(description = "ID карты", required = true) @PathVariable("id") Long id) {
        CardDTO cardDTO = cardService.getCard(id);
        return new ResponseEntity<>(cardDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}/userCards")
    @Operation(summary = "Получить карты пользователя", description = "Возвращает список карт, принадлежащих пользователю, с пагинацией. Доступно только администратору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Page<CardDTO>> getUserCards(
            @Parameter(description = "ID пользователя", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Возвращает список карт, принадлежащих авторизованному пользователю, с пагинацией.", required = true) @Valid @NotNull Pageable pageable) {
        Page<CardDTO> userCards = cardService.getUserCards(id, pageable);
        return new ResponseEntity<>(userCards, HttpStatus.OK);
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Получить баланс карты", description = "Возвращает баланс карты по её ID. Доступно для авторизованных пользователей.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс карты успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    public ResponseEntity<CardResponseBalanceDTO> getBalance(
            @Parameter(description = "ID карты", required = true) @PathVariable("id") Long id) {
        CardResponseBalanceDTO cardResponseBalanceDTO = cardService.getBalanceCardByCardId(id);
        return new ResponseEntity<>(cardResponseBalanceDTO, HttpStatus.OK);
    }

    @PatchMapping("/block/{id}")
    @Operation(summary = "Запрос на блокировку карты", description = "Отправляет запрос на блокировку карты по её ID. Доступно для авторизованных пользователей.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на блокировку успешно отправлен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    public ResponseEntity<CardResponseBlockDTO> requestToBlockCard(
            @Parameter(description = "ID карты", required = true) @PathVariable("id") Long id) {
        log.info("Requesting block for card id: {}", id);
        CardResponseBlockDTO response = cardService.requestBlock(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    @Operation(summary = "Получить все карты всех пользователей", description = "Возвращает список абсолютно всех банковских карт с поддержкой пагинации. Доступно только администратору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public Page<CardDTO> getAllCards(
            @Parameter(description = "Параметры пагинации (page, size, sort)", required = true) Pageable pageable) {
        return cardService.getAllCards(pageable);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/statuses/request/cards")
    @Operation(summary = "Получить статусы запросов на блокировку карт", description = "Возвращает список статусов запросов на блокировку карт с пагинацией. Доступно только администратору.")
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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить карту", description = "Удаляет банковскую карту по её ID. Доступно только администратору.")
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


}