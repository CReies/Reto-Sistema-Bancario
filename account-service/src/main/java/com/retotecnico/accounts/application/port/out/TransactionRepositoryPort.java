package com.retotecnico.accounts.application.port.out;

import com.retotecnico.accounts.domain.model.Transaction;
import java.time.OffsetDateTime;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepositoryPort {

    Mono<Transaction> findById(Long id);

    <S extends Transaction> Mono<S> save(S transaction);

    Mono<Void> delete(Transaction transaction);

    Flux<Transaction> findByAccountIdOrderByDateAsc(Long accountId);

    Flux<TransactionReportRow> findReport(String clientId, OffsetDateTime from, OffsetDateTime to);

    record TransactionReportRow(
            OffsetDateTime date,
            String client,
            String accountNumber,
            String type,
            java.math.BigDecimal initialBalance,
            Boolean active,
            java.math.BigDecimal transaction,
            java.math.BigDecimal availableBalance
    ) {
    }
}
