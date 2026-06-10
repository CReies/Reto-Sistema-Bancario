package com.retotecnico.accounts.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionRequest(
        @NotBlank @Size(max = 30) String accountNumber,
        @NotBlank @Size(max = 40) String transactionType,
        @NotNull BigDecimal amount,
        OffsetDateTime date
) {
}
