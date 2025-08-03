package com.example.bankcards.controller;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferEntityDTO;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
@Tag(name = "Transfer API", description = "API для выполнения переводов между банковскими картами")
@SecurityRequirement(name = "bearerAuth")
public class TransferController {
    private final TransferService transferService;

    @PostMapping()
    @Operation(summary = "Выполнить перевод", description = "Выполняет перевод средств между картами пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "422", description = "Недостаточно средств или карта заблокирована(BLOCK, EXPIRED)")
    })
    public ResponseEntity<TransferEntityDTO> transfer(@Valid @RequestBody TransferDTO transferDTO) {
        try {
            TransferEntityDTO transferEntityDTO = transferService.transfer(transferDTO);
            return new ResponseEntity<>(transferEntityDTO, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY); // 422
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400
        }
    }
}