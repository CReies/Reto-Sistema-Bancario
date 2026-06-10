package com.retotecnico.accounts.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    private Long id;

    @Column("account_id")
    private Long accountId;

    private OffsetDateTime date;

    @Column("transaction_type")
    private String transactionType;

    private BigDecimal amount;

    private BigDecimal balance;
}
