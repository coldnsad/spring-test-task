package org.example.springtesttask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springtesttask.dto.ProcessWalletDto;
import org.example.springtesttask.service.WalletService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/wallet/create")
    public ResponseEntity<UUID> createWallet(){
        UUID walletId = walletService.createWallet();
        return ResponseEntity.status(HttpStatus.CREATED).body(walletId);
    }

    @PostMapping("/wallet")
    public void processWallet(@Valid @RequestBody ProcessWalletDto dto){
        walletService.processWallet(dto);
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<Integer> getWalletBalance(@Valid @PathVariable UUID walletId){
        return ResponseEntity.ok().body(walletService.getWalletBalance(walletId));
    }
}
