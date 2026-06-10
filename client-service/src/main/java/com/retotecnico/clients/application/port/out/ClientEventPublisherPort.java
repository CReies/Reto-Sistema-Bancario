package com.retotecnico.clients.application.port.out;

import com.retotecnico.clients.domain.event.ClientChangedEvent;
import reactor.core.publisher.Mono;

public interface ClientEventPublisherPort {

    Mono<Void> publish(ClientChangedEvent event);
}
