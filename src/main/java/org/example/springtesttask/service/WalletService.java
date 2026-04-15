package org.example.springtesttask.service;

import jakarta.validation.Valid;
import org.example.springtesttask.dto.ProcessWalletDto;

import java.util.UUID;

public interface WalletService {
    UUID createWallet();

    void processWallet(ProcessWalletDto dto);

    Integer getWalletBalance(UUID walletId);
}
