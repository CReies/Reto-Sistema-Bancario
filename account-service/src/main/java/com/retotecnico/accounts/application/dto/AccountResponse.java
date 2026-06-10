package com.retotecnico.accounts.application.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        String accountType,
        BigDecimal initialBalance,
        BigDecimal currentBalance,
        Boolean active,
        String clientId
) {
}
