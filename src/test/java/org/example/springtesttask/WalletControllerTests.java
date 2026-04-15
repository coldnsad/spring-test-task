package org.example.springtesttask;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.springtesttask.controller.WalletController;
import org.example.springtesttask.dto.OperationType;
import org.example.springtesttask.dto.ProcessWalletDto;
import org.example.springtesttask.exception.NoSuchWalletException;
import org.example.springtesttask.service.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@DisplayName("Тестирование Rest endpoints")
class WalletControllerTests {
    private static final String properUuid = "19a2e3f9-39f1-4d3b-ae60-c4046665aeb4";
    private static final String wrongUuid = "19a2e3f9-39f1-4d3b-ae60-c4046665aeb4sdffsdfsdfsd";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    WalletService walletService;

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть статус 201 и созданный UUID в случае если кошелёк успешно создан")
    void shouldReturn201WhenWalletCreated() {
        //Подготовка
        UUID expectedUuidResponse = UUID.fromString(properUuid);

        when(walletService.createWallet()).thenReturn(expectedUuidResponse);
        //Выполнение и проверка
        mockMvc.perform(post("/api/v1/wallet/create"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(properUuid));
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть статус 200 при пополнении баланса")
    void shouldReturn200WhenPerformDeposit() {
        //Подготовка
        ProcessWalletDto dto = ProcessWalletDto.builder()
                .walletId(UUID.fromString(properUuid))
                .operationType(OperationType.DEPOSIT)
                .amount(500)
                .build();

        //Выполнение и проверка
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
        //Проверка вызова метода сервиса один раз
        verify(walletService, times(1)).processWallet((any(ProcessWalletDto.class)));
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть статус 200 и баланс на счету кошелька")
    void shouldReturn200AndBalanceWhenGetBalance() {
        UUID uuid = UUID.fromString(properUuid);
        //Подготовка
        Integer expectedBalance = 1000;
        when(walletService.getWalletBalance(uuid)).thenReturn(expectedBalance);

        //Выполнение и проверка
        mockMvc.perform(get("/api/v1/wallets/{walletId}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedBalance));

        verify(walletService).getWalletBalance(uuid);
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть статус 400 при попытке получить баланс несуществующего кошелька")
    void shouldReturn400WhenGetBalanceWithNonExistedWallet() {
        UUID nonExistedUuid = UUID.fromString(properUuid);
        //Подготовка
        when(walletService.getWalletBalance(nonExistedUuid)).thenThrow(
                new NoSuchWalletException()
        );

        //Выполнение и проверка
        mockMvc.perform(get("/api/v1/wallets/{walletId}", nonExistedUuid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg")
                        .value("Кошелёк с данным walletId отсутствует в системе"));

        verify(walletService).getWalletBalance(nonExistedUuid);
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть статус 400 при использовании неверного UUID")
    void shouldReturn400WhenUUIDIsIncorrect() {
        //Выполнение и проверка
        mockMvc.perform(get("/api/v1/wallets/{walletId}", wrongUuid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.*", hasItem("Некорректный UUID формат")));
    }

}
