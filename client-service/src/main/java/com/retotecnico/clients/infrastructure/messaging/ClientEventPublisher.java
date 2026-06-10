package com.retotecnico.clients.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retotecnico.clients.application.port.out.ClientEventPublisherPort;
import com.retotecnico.clients.domain.event.ClientChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

@Component
public class ClientEventPublisher implements ClientEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(ClientEventPublisher.class);

    private final Sender sender;
    private final ObjectMapper objectMapper;
    private final String exchange;
    private final String routingKey;

    public ClientEventPublisher(
            Sender sender,
            ObjectMapper objectMapper,
            @Value("${app.messaging.exchange}") String exchange,
            @Value("${app.messaging.routing-key}") String routingKey) {
        this.sender = sender;
        this.objectMapper = objectMapper;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public Mono<Void> publish(ClientChangedEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(event))
                .map(body -> new OutboundMessage(exchange, routingKey, body))
                .flatMap(message -> sender.send(Mono.just(message)))
                .doOnError(ex -> log.warn("Could not publish client event {}. An outbox retry would be used in a production evolution.",
                        event.clientId(), ex))
                .onErrorResume(ex -> Mono.empty());
    }
}
