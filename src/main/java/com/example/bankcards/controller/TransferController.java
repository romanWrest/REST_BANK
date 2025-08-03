package com.example.bankcards.controller;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferEntityDTO;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {
    private TransferService transferService;

    @PostMapping()
    public ResponseEntity<TransferEntityDTO> transfer(@Valid @RequestBody TransferDTO transferDTO) {
        TransferEntityDTO transferEntityDTO = transferService.transfer(transferDTO);
        return  new ResponseEntity<>(transferEntityDTO, HttpStatus.OK);
    }
}
