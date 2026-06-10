package com.retotecnico.accounts.application.service.account;

import com.retotecnico.accounts.application.dto.AccountResponse;
import com.retotecnico.accounts.application.mapper.AccountMapper;
import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class FindAllAccountService {

    private final AccountRepositoryPort accountRepository;
    private final AccountMapper mapper;

    @Transactional(readOnly = true)
    public Flux<AccountResponse> execute() {
        return accountRepository.findAll().map(mapper::toResponse);
    }
}
