package com.retotecnico.accounts.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import com.retotecnico.accounts.application.port.out.ClientProjectionRepositoryPort;
import com.retotecnico.accounts.domain.model.ClientProjection;
import com.retotecnico.accounts.infrastructure.messaging.dto.ClientChangedEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.messaging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ClientProjectionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClientProjectionEventConsumer.class);

    private final ClientProjectionRepositoryPort repository;
    private final Receiver receiver;
    private final ObjectMapper objectMapper;

    @Value("${app.messaging.queue}")
    private String queueName;

    private Disposable subscription;

    @PostConstruct
    void start() {
        subscription = receiver.consumeAutoAck(queueName)
                .flatMap(delivery -> consumeDelivery(delivery)
                        .onErrorResume(ex -> {
                            log.warn("Could not consume client projection event", ex);
                            return Mono.empty();
                        }))
                .subscribe();
    }

    @PreDestroy
    void stop() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    private Mono<Void> consumeDelivery(Delivery delivery) {
        return Mono.fromCallable(() -> objectMapper.readValue(delivery.getBody(), ClientChangedEvent.class))
                .flatMap(this::consume);
    }

    @Transactional
    public Mono<Void> consume(ClientChangedEvent event) {
        if ("DELETED".equalsIgnoreCase(event.eventType())) {
            return repository.deleteById(event.clientId());
        }
        return repository.findById(event.clientId())
                .defaultIfEmpty(new ClientProjection())
                .flatMap(projection -> {
                    projection.setClientId(event.clientId());
                    projection.setName(event.name());
                    projection.setIdentification(event.identification());
                    projection.setActive(event.active());
                    return repository.save(projection);
                })
                .then();
    }
}
