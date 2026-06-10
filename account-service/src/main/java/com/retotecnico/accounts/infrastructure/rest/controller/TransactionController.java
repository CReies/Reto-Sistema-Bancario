package com.retotecnico.accounts.infrastructure.rest.controller;

import com.retotecnico.accounts.application.dto.TransactionRequest;
import com.retotecnico.accounts.application.dto.TransactionResponse;
import com.retotecnico.accounts.application.service.transaction.CreateTransactionService;
import com.retotecnico.accounts.application.service.transaction.DeleteTransactionService;
import com.retotecnico.accounts.application.service.transaction.FindTransactionByIdService;
import com.retotecnico.accounts.application.service.transaction.FindTransactionsByAccountService;
import com.retotecnico.accounts.application.service.transaction.UpdateTransactionService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final FindTransactionsByAccountService findTransactionsByAccountService;
    private final FindTransactionByIdService findTransactionByIdService;
    private final CreateTransactionService createTransactionService;
    private final UpdateTransactionService updateTransactionService;
    private final DeleteTransactionService deleteTransactionService;

    @GetMapping
    public Flux<TransactionResponse> findByAccount(@RequestParam String accountNumber) {
        return findTransactionsByAccountService.execute(accountNumber);
    }

    @GetMapping("/{id}")
    public Mono<TransactionResponse> findById(@PathVariable Long id) {
        return findTransactionByIdService.execute(id);
    }

    @PostMapping
    public Mono<ResponseEntity<TransactionResponse>> create(@Valid @RequestBody TransactionRequest request) {
        return createTransactionService.execute(request)
                .map(response -> ResponseEntity.created(URI.create("/api/transactions/" + response.id())).body(response));
    }

    @PutMapping("/{id}")
    public Mono<TransactionResponse> update(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        return updateTransactionService.execute(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return deleteTransactionService.execute(id).thenReturn(ResponseEntity.noContent().build());
    }
}
