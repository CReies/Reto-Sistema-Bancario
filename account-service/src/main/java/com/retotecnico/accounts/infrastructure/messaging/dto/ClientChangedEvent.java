package com.retotecnico.accounts.infrastructure.messaging.dto;

public record ClientChangedEvent(
        String eventType,
        Long id,
        String clientId,
        String name,
        String identification,
        Boolean active
) {
}
