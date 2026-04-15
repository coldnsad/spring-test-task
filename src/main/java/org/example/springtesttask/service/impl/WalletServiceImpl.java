package org.example.springtesttask.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.springtesttask.dto.OperationType;
import org.example.springtesttask.dto.ProcessWalletDto;
import org.example.springtesttask.entity.Wallet;
import org.example.springtesttask.exception.InsufficientBalanceException;
import org.example.springtesttask.exception.NoSuchWalletException;
import org.example.springtesttask.repository.WalletRepository;
import org.example.springtesttask.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public UUID createWallet() {
        Wallet wallet = new Wallet();
        wallet.setBalance(0);
        Wallet newWallet = walletRepository.save(wallet);
        return newWallet.getId();
    }

    @Override
    @Transactional
    public void processWallet(ProcessWalletDto dto) {
        OperationType operationType = dto.operationType();
        if(operationType == OperationType.DEPOSIT) depositWallet(dto);
        else withdrawWallet(dto);
    }

    @Override
    @Transactional
    public Integer getWalletBalance(UUID walletId) {
        Wallet wallet = walletRepository.getWithLockingById(walletId).orElseThrow(NoSuchWalletException::new);
        return wallet.getBalance();
    }

    private void withdrawWallet(ProcessWalletDto dto) {
        Wallet wallet = walletRepository.getWithLockingById(dto.walletId()).orElseThrow(NoSuchWalletException::new);
        int currentBalance = wallet.getBalance();
        int amount = dto.amount();
        if(currentBalance - amount < 0){
            throw new InsufficientBalanceException();
        }
        wallet.setBalance(currentBalance - amount);
        walletRepository.save(wallet);
    }

    private void depositWallet(ProcessWalletDto dto) {
        Wallet wallet = walletRepository.getWithLockingById(dto.walletId()).orElseThrow(NoSuchWalletException::new);
        wallet.setBalance(wallet.getBalance() + dto.amount());
        walletRepository.save(wallet);
    }


}
