package com.retotecnico.accounts.application.service.account;

import com.retotecnico.accounts.application.dto.AccountResponse;
import com.retotecnico.accounts.application.mapper.AccountMapper;
import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.domain.exception.ResourceNotFoundException;
import com.retotecnico.accounts.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FindAccountByIdService {

    private final AccountRepositoryPort accountRepository;
    private final AccountMapper mapper;

    @Transactional(readOnly = true)
    public Mono<AccountResponse> execute(Long id) {
        return findAccount(id).map(mapper::toResponse);
    }

    Mono<Account> findAccount(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + id)));
    }
}
