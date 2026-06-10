package com.retotecnico.accounts.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AccountRequest(
        @NotBlank @Size(max = 30) String accountNumber,
        @NotBlank @Size(max = 40) String accountType,
        @NotNull @DecimalMin(value = "0.00") BigDecimal initialBalance,
        @NotNull Boolean active,
        @NotBlank @Size(max = 50) String clientId
) {
}
