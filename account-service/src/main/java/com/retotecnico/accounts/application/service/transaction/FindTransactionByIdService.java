package com.retotecnico.accounts.application.service.transaction;

import com.retotecnico.accounts.application.dto.TransactionResponse;
import com.retotecnico.accounts.application.mapper.TransactionMapper;
import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.application.port.out.TransactionRepositoryPort;
import com.retotecnico.accounts.domain.exception.ResourceNotFoundException;
import com.retotecnico.accounts.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FindTransactionByIdService {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;
    private final TransactionMapper mapper;

    @Transactional(readOnly = true)
    public Mono<TransactionResponse> execute(Long id) {
        return findTransaction(id)
                .flatMap(transaction -> accountRepository.findById(transaction.getAccountId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + transaction.getAccountId())))
                        .map(account -> mapper.toResponse(transaction, account.getAccountNumber())));
    }

    private Mono<Transaction> findTransaction(Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Transaction not found: " + id)));
    }
}
