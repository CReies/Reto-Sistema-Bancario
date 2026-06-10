package com.retotecnico.accounts.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(
        Long id,
        String accountNumber,
        OffsetDateTime date,
        String transactionType,
        BigDecimal amount,
        BigDecimal balance
) {
}
