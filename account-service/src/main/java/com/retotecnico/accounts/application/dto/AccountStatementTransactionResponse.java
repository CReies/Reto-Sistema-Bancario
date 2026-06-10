package com.retotecnico.accounts.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AccountStatementTransactionResponse(
        OffsetDateTime date,
        String client,
        String accountNumber,
        String type,
        BigDecimal initialBalance,
        Boolean active,
        BigDecimal transaction,
        BigDecimal availableBalance
) {
}
