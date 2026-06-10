package com.retotecnico.accounts.infrastructure.persistence.repository;

import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.domain.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long>, AccountRepositoryPort {

    Mono<Boolean> existsByAccountNumber(String accountNumber);

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findByClientId(String clientId);
}
