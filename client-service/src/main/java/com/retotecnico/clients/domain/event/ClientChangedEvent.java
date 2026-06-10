package com.retotecnico.clients.domain.event;

public record ClientChangedEvent(
        String eventType,
        Long id,
        String clientId,
        String name,
        String identification,
        Boolean active
) {
}
