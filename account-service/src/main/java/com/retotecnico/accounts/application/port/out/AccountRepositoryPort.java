package com.retotecnico.accounts.application.port.out;

import com.retotecnico.accounts.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepositoryPort {

    Flux<Account> findAll();

    Mono<Account> findById(Long id);

    <S extends Account> Mono<S> save(S account);

    Mono<Void> delete(Account account);

    Mono<Boolean> existsByAccountNumber(String accountNumber);

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findByClientId(String clientId);
}
