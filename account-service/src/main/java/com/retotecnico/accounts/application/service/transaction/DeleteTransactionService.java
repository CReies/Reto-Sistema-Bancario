package com.retotecnico.accounts.application.service.transaction;

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
public class DeleteTransactionService {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;

    @Transactional
    public Mono<Void> execute(Long id) {
        return findTransaction(id)
                .flatMap(transaction -> accountRepository.findById(transaction.getAccountId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + transaction.getAccountId())))
                        .flatMap(account -> deleteTransaction(transaction, account)));
    }

    private Mono<Void> deleteTransaction(Transaction transaction, Account account) {
        BigDecimal revertedBalance = account.getCurrentBalance().subtract(transaction.getAmount());
        if (revertedBalance.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new BusinessException("Cannot delete the transaction because it would affect the available balance"));
        }
        account.setCurrentBalance(revertedBalance);
        return accountRepository.save(account)
                .then(transactionRepository.delete(transaction));
    }

    private Mono<Transaction> findTransaction(Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Transaction not found: " + id)));
    }
}
