package com.retotecnico.clients.infrastructure.persistence.repository;

import com.retotecnico.clients.application.port.out.ClientRepositoryPort;
import com.retotecnico.clients.domain.model.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ClientRepository extends ReactiveCrudRepository<Client, Long>, ClientRepositoryPort {

    Mono<Boolean> existsByClientId(String clientId);

    Mono<Boolean> existsByIdentification(String identification);

    Mono<Client> findByClientId(String clientId);
}
