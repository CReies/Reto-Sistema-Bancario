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
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateTransactionService {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;
    private final TransactionMapper mapper;

    @Transactional
    public Mono<TransactionResponse> execute(TransactionRequest request) {
        return accountRepository.findByAccountNumber(request.accountNumber())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Account not found: " + request.accountNumber())))
                .flatMap(account -> createTransaction(account, request));
    }

    private Mono<TransactionResponse> createTransaction(Account account, TransactionRequest request) {
        if (!Boolean.TRUE.equals(account.getActive())) {
            return Mono.error(new BusinessException("Account inactive: " + request.accountNumber()));
        }

        BusinessException amountValidationError = validateTransactionAmount(request);

        if (amountValidationError != null) {
            return Mono.error(amountValidationError);
        }

        BigDecimal newBalance = account.getCurrentBalance().add(request.amount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new BusinessException("Insufficient balance"));
        }

        account.setCurrentBalance(newBalance);
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId());
        transaction.setDate(request.date() == null ? OffsetDateTime.now() : request.date());
        transaction.setTransactionType(request.transactionType());
        transaction.setAmount(request.amount());
        transaction.setBalance(newBalance);

        return accountRepository.save(account)
                .then(transactionRepository.save(transaction))
                .map(saved -> mapper.toResponse(saved, account.getAccountNumber()));
    }

    private BusinessException validateTransactionAmount(TransactionRequest request) {
        if ("Withdrawal".equals(request.transactionType())
                && request.amount().compareTo(BigDecimal.ZERO) >= 0) {
            return new BusinessException("Withdrawal amount must be negative");
        }

        if ("Deposit".equals(request.transactionType())
                && request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return new BusinessException("Deposit amount must be positive");
        }

        return null;
    }
}
