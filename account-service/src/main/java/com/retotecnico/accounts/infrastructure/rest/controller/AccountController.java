package com.retotecnico.accounts.infrastructure.rest.controller;

import com.retotecnico.accounts.application.dto.AccountRequest;
import com.retotecnico.accounts.application.dto.AccountResponse;
import com.retotecnico.accounts.application.service.account.CreateAccountService;
import com.retotecnico.accounts.application.service.account.DeleteAccountService;
import com.retotecnico.accounts.application.service.account.FindAccountByIdService;
import com.retotecnico.accounts.application.service.account.FindAllAccountService;
import com.retotecnico.accounts.application.service.account.UpdateAccountService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final FindAllAccountService findAllAccountService;
    private final FindAccountByIdService findAccountByIdService;
    private final CreateAccountService createAccountService;
    private final UpdateAccountService updateAccountService;
    private final DeleteAccountService deleteAccountService;

    @GetMapping
    public Flux<AccountResponse> findAll() {
        return findAllAccountService.execute();
    }

    @GetMapping("/{id}")
    public Mono<AccountResponse> findById(@PathVariable Long id) {
        return findAccountByIdService.execute(id);
    }

    @PostMapping
    public Mono<ResponseEntity<AccountResponse>> create(@Valid @RequestBody AccountRequest request) {
        return createAccountService.execute(request)
                .map(response -> ResponseEntity.created(URI.create("/api/accounts/" + response.id())).body(response));
    }

    @PutMapping("/{id}")
    public Mono<AccountResponse> update(@PathVariable Long id, @Valid @RequestBody AccountRequest request) {
        return updateAccountService.execute(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return deleteAccountService.execute(id).thenReturn(ResponseEntity.noContent().build());
    }
}
