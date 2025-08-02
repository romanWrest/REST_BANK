package com.example.bankcards.controller;

import com.example.bankcards.dto.Transfer.TransferDTO;
import com.example.bankcards.dto.Transfer.TransferEntityDTO;
import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {
    private TransferService transferService;

    @PostMapping()
    public ResponseEntity<TransferEntityDTO> transfer(TransferDTO transferDTO) {
        TransferEntityDTO transferEntityDTO = transferService.transfer(transferDTO);
        return  new ResponseEntity<>(transferEntityDTO, HttpStatus.OK);
    }
}
