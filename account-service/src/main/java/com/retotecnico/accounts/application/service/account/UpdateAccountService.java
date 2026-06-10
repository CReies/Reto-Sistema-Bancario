package com.retotecnico.accounts.application.service.account;

import com.retotecnico.accounts.application.dto.AccountRequest;
import com.retotecnico.accounts.application.dto.AccountResponse;
import com.retotecnico.accounts.application.mapper.AccountMapper;
import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.application.port.out.ClientProjectionRepositoryPort;
import com.retotecnico.accounts.domain.exception.BusinessException;
import com.retotecnico.accounts.domain.exception.ResourceNotFoundException;
import com.retotecnico.accounts.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateAccountService {

    private final AccountRepositoryPort accountRepository;
    private final ClientProjectionRepositoryPort clientRepository;
    private final AccountMapper mapper;

    @Transactional
    public Mono<AccountResponse> execute(Long id, AccountRequest request) {
        return findAccount(id)
                .flatMap(account -> validateActiveClient(request.clientId()).thenReturn(account))
                .flatMap(account -> validateUniqueAccountNumber(account, request).thenReturn(account))
                .flatMap(account -> {
                    mapper.updateEntity(account, request);
                    return accountRepository.save(account);
                })
                .map(mapper::toResponse);
    }

    private Mono<Account> findAccount(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + id)));
    }

    private Mono<Void> validateActiveClient(String clientId) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new BusinessException("Client does not exist in the local projection: " + clientId)))
                .flatMap(client -> Boolean.TRUE.equals(client.getActive())
                        ? Mono.empty()
                        : Mono.error(new BusinessException("Client inactive: " + clientId)));
    }

    private Mono<Void> validateUniqueAccountNumber(Account account, AccountRequest request) {
        if (account.getAccountNumber().equals(request.accountNumber())) {
            return Mono.empty();
        }
        return accountRepository.existsByAccountNumber(request.accountNumber())
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException("The account number already exists"))
                        : Mono.empty());
    }
}
