package com.retotecnico.accounts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.retotecnico.accounts.application.dto.TransactionRequest;
import com.retotecnico.accounts.application.mapper.TransactionMapper;
import com.retotecnico.accounts.application.service.transaction.CreateTransactionService;
import com.retotecnico.accounts.domain.model.Account;
import com.retotecnico.accounts.domain.model.Transaction;
import com.retotecnico.accounts.domain.exception.BusinessException;
import com.retotecnico.accounts.application.port.out.AccountRepositoryPort;
import com.retotecnico.accounts.application.port.out.TransactionRepositoryPort;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private AccountRepositoryPort accountRepository;

    private CreateTransactionService service;

    @BeforeEach
    void setUp() {
        service = new CreateTransactionService(
                transactionRepository,
                accountRepository,
                Mappers.getMapper(TransactionMapper.class)
        );
    }

    @Test
    void createRejectsMovementWithoutBalance() {
        Account account = account(new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Mono.just(account));

        var request = new TransactionRequest("478758", "Withdrawal", new BigDecimal("-575.00"), null);

        StepVerifier.create(service.execute(request))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage("Insufficient balance"))
                .verify();
    }

    @Test
    void createRejectsWithdrawalWithPositiveAmount() {
        Account account = account(new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Mono.just(account));

        var request = new TransactionRequest("478758", "Withdrawal", new BigDecimal("50.00"), null);

        StepVerifier.create(service.execute(request))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage("Withdrawal amount must be negative"))
                .verify();
    }

    @Test
    void createRejectsDepositWithNegativeAmount() {
        Account account = account(new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Mono.just(account));

        var request = new TransactionRequest("478758", "Deposit", new BigDecimal("-50.00"), null);

        StepVerifier.create(service.execute(request))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage("Deposit amount must be positive"))
                .verify();
    }

    @Test
    void createUpdatesBalanceAndStoresMovement() {
        Account account = account(new BigDecimal("100.00"));
        account.setAccountNumber("225487");
        when(accountRepository.findByAccountNumber("225487")).thenReturn(Mono.just(account));
        when(accountRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(transactionRepository.save(any())).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(1L);
            return Mono.just(transaction);
        });

        var request = new TransactionRequest("225487", "Deposit", new BigDecimal("600.00"), null);

        StepVerifier.create(service.execute(request))
                .assertNext(response -> {
                    assertThat(response.balance()).isEqualByComparingTo("700.00");
                    assertThat(account.getCurrentBalance()).isEqualByComparingTo("700.00");
                })
                .verifyComplete();
    }

    private Account account(BigDecimal balance) {
        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("478758");
        account.setAccountType("Savings");
        account.setInitialBalance(balance);
        account.setCurrentBalance(balance);
        account.setActive(true);
        account.setClientId("CLI-1");
        return account;
    }
}
