package com.retotecnico.clients.application.dto;

public record ClientResponse(
        Long id,
        String clientId,
        String name,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone,
        Boolean active
) {
}
