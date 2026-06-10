package com.retotecnico.clients.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank @Size(max = 50) String clientId,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 30) String gender,
        @NotNull @Min(0) Integer age,
        @NotBlank @Size(max = 40) String identification,
        @NotBlank @Size(max = 180) String address,
        @NotBlank @Size(max = 40) String phone,
        @NotBlank @Size(max = 120) String password,
        @NotNull Boolean active
) {
}
