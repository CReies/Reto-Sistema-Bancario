package com.retotecnico.accounts.application.service.account;

import com.retotecnico.accounts.application.dto.AccountRequest;
import com.retotecnico.accounts.application.dto.AccountResponse;
import com.retotecnico.accounts.application.mapper.AccountMapper;
import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.application.port.out.ClientProjectionRepositoryPort;
import com.retotecnico.accounts.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateAccountService {

    private final AccountRepositoryPort accountRepository;
    private final ClientProjectionRepositoryPort clientRepository;
    private final AccountMapper mapper;

    @Transactional
    public Mono<AccountResponse> execute(AccountRequest request) {
        return validateActiveClient(request.clientId())
                .then(accountRepository.existsByAccountNumber(request.accountNumber()))
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException("The account number already exists"))
                        : accountRepository.save(mapper.toEntity(request)))
                .map(mapper::toResponse);
    }

    private Mono<Void> validateActiveClient(String clientId) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new BusinessException("Client does not exist in the local projection: " + clientId)))
                .flatMap(client -> Boolean.TRUE.equals(client.getActive())
                        ? Mono.empty()
                        : Mono.error(new BusinessException("Client inactive: " + clientId)));
    }
}
