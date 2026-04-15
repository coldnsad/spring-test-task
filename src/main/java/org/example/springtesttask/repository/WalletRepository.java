package org.example.springtesttask.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.springtesttask.entity.Wallet;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    //Пессимистичная блокировка, приводит к блокировке строки в БД, другие потоки ожидают
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> getWithLockingById(UUID id);
}
