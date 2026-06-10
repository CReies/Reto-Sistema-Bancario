package com.retotecnico.accounts.application.service;

import com.retotecnico.accounts.application.dto.AccountStatementTransactionResponse;
import com.retotecnico.accounts.application.port.out.TransactionRepositoryPort;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepositoryPort transactionRepository;

    @Transactional(readOnly = true)
    public Flux<AccountStatementTransactionResponse> accountStatement(String clientId, OffsetDateTime from, OffsetDateTime to) {
        return transactionRepository.findReport(clientId, from, to)
                .map(row -> new AccountStatementTransactionResponse(
                        row.date(),
                        row.client(),
                        row.accountNumber(),
                        row.type(),
                        row.initialBalance(),
                        row.active(),
                        row.transaction(),
                        row.availableBalance()
                ));
    }
}
