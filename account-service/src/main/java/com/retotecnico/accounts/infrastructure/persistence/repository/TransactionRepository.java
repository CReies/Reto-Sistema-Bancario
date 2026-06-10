package com.retotecnico.accounts.infrastructure.persistence.repository;

import com.retotecnico.accounts.application.port.out.TransactionRepositoryPort;
import com.retotecnico.accounts.domain.model.Transaction;
import java.time.OffsetDateTime;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long>, TransactionRepositoryPort {

    Flux<Transaction> findByAccountIdOrderByDateAsc(Long accountId);

    @Query("""
            select t.date,
                   coalesce(cp.name, a.client_id) as client,
                   a.account_number,
                   a.account_type as type,
                   a.initial_balance,
                   a.active,
                   t.amount as transaction,
                   t.balance as available_balance
            from transactions t
            join accounts a on a.id = t.account_id
            left join client_projections cp on cp.client_id = a.client_id
            where a.client_id = :clientId
              and t.date between :from and :to
            order by t.date asc
            """)
    Flux<TransactionReportRow> findReport(
            @Param("clientId") String clientId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to);
}
