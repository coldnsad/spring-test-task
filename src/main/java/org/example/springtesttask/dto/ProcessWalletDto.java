package org.example.springtesttask.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ProcessWalletDto(
        @NotNull(message = "Проверьте правильность написания имени поля, также поле не должно быть пустым")
        UUID walletId,
        @NotNull(message = "Проверьте правильность написания имени поля, также поле не должно быть пустым")
        OperationType operationType,
        @NotNull(message = "Проверьте правильность написания имени поля, также поле не должно быть пустым")
        @Min(0)
        Integer amount
) { }
