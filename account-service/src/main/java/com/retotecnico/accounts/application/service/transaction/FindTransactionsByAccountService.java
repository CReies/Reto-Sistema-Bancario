package com.retotecnico.accounts.application.service.transaction;

import com.retotecnico.accounts.application.dto.TransactionResponse;
import com.retotecnico.accounts.application.mapper.TransactionMapper;
import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.application.port.out.TransactionRepositoryPort;
import com.retotecnico.accounts.domain.exception.ResourceNotFoundException;
import com.retotecnico.accounts.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FindTransactionsByAccountService {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;
    private final TransactionMapper mapper;

    @Transactional(readOnly = true)
    public Flux<TransactionResponse> execute(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + accountNumber)))
                .flatMapMany(this::findTransactions);
    }

    private Flux<TransactionResponse> findTransactions(Account account) {
        return transactionRepository.findByAccountIdOrderByDateAsc(account.getId())
                .map(transaction -> mapper.toResponse(transaction, account.getAccountNumber()));
    }
}
