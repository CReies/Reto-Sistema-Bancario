package com.retotecnico.accounts.application.service.transaction;

import com.retotecnico.accounts.application.dto.TransactionRequest;
import com.retotecnico.accounts.application.dto.TransactionResponse;
import com.retotecnico.accounts.application.mapper.TransactionMapper;
import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.application.port.out.TransactionRepositoryPort;
import com.retotecnico.accounts.domain.exception.BusinessException;
import com.retotecnico.accounts.domain.exception.ResourceNotFoundException;
import com.retotecnico.accounts.domain.model.Account;
import com.retotecnico.accounts.domain.model.Transaction;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateTransactionService {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;
    private final TransactionMapper mapper;

    @Transactional
    public Mono<TransactionResponse> execute(Long id, TransactionRequest request) {
        return findTransaction(id)
                .flatMap(transaction -> accountRepository.findById(transaction.getAccountId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + transaction.getAccountId())))
                        .flatMap(previousAccount -> updateTransaction(transaction, previousAccount, request)));
    }

    private Mono<TransactionResponse> updateTransaction(Transaction transaction, Account previousAccount, TransactionRequest request) {
        BigDecimal revertedBalance = previousAccount.getCurrentBalance().subtract(transaction.getAmount());
        if (revertedBalance.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new BusinessException("Cannot update the transaction because it would affect the available balance"));
        }
        previousAccount.setCurrentBalance(revertedBalance);

        return accountRepository.findByAccountNumber(request.accountNumber())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + request.accountNumber())))
                .flatMap(newAccount -> applyTransactionUpdate(transaction, previousAccount, newAccount, request));
    }

    private Mono<TransactionResponse> applyTransactionUpdate(
            Transaction transaction,
            Account previousAccount,
            Account newAccount,
            TransactionRequest request) {
        if (previousAccount.getId().equals(newAccount.getId())) {
            newAccount.setCurrentBalance(previousAccount.getCurrentBalance());
        }
        BigDecimal newBalance = newAccount.getCurrentBalance().add(request.amount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new BusinessException("Insufficient balance"));
        }

        newAccount.setCurrentBalance(newBalance);
        transaction.setAccountId(newAccount.getId());
        transaction.setDate(request.date() == null ? transaction.getDate() : request.date());
        transaction.setTransactionType(request.transactionType());
        transaction.setAmount(request.amount());
        transaction.setBalance(newBalance);

        Mono<Account> savePreviousAccount = previousAccount.getId().equals(newAccount.getId())
                ? Mono.just(newAccount)
                : accountRepository.save(previousAccount);

        return savePreviousAccount
                .then(accountRepository.save(newAccount))
                .then(transactionRepository.save(transaction))
                .map(saved -> mapper.toResponse(saved, newAccount.getAccountNumber()));
    }

    private Mono<Transaction> findTransaction(Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Transaction not found: " + id)));
    }
}
