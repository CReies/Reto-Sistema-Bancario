package com.retotecnico.accounts.infrastructure.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retotecnico.accounts.application.dto.TransactionRequest;
import com.retotecnico.accounts.application.dto.TransactionResponse;
import com.retotecnico.accounts.domain.exception.BusinessException;
import com.retotecnico.accounts.application.service.transaction.CreateTransactionService;
import com.retotecnico.accounts.application.service.transaction.DeleteTransactionService;
import com.retotecnico.accounts.application.service.transaction.FindTransactionByIdService;
import com.retotecnico.accounts.application.service.transaction.FindTransactionsByAccountService;
import com.retotecnico.accounts.application.service.transaction.UpdateTransactionService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindTransactionsByAccountService findTransactionsByAccountService;

    @MockBean
    private FindTransactionByIdService findTransactionByIdService;

    @MockBean
    private CreateTransactionService createTransactionService;

    @MockBean
    private UpdateTransactionService updateTransactionService;

    @MockBean
    private DeleteTransactionService deleteTransactionService;

    @Test
    void createReturnsCreatedMovement() throws Exception {
        var request = new TransactionRequest("225487", "Deposit", new BigDecimal("600.00"), null);
        var response = new TransactionResponse(1L, "225487", OffsetDateTime.now(),
                "Deposit", new BigDecimal("600.00"), new BigDecimal("700.00"));
        when(createTransactionService.execute(any())).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(700.00);
    }

    @Test
    void createReturnsBusinessErrorWhenBalanceIsMissing() throws Exception {
        var request = new TransactionRequest("478758", "Withdrawal", new BigDecimal("-575.00"), null);
        when(createTransactionService.execute(any())).thenReturn(Mono.error(new BusinessException("Insufficient balance")));

        webTestClient.post()
                .uri("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Insufficient balance");
    }
}
