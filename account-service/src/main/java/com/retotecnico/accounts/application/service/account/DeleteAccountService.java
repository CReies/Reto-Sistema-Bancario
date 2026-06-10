package com.retotecnico.accounts.application.service.account;

import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.domain.exception.ResourceNotFoundException;
import com.retotecnico.accounts.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteAccountService {

    private final AccountRepositoryPort accountRepository;

    @Transactional
    public Mono<Void> execute(Long id) {
        return findAccount(id).flatMap(accountRepository::delete);
    }

    private Mono<Account> findAccount(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + id)));
    }
}
