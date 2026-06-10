package com.retotecnico.clients.application.port.out;

import com.retotecnico.clients.domain.model.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRepositoryPort {

    Flux<Client> findAll();

    Mono<Client> findById(Long id);

    <S extends Client> Mono<S> save(S client);

    Mono<Void> delete(Client client);

    Mono<Boolean> existsByClientId(String clientId);

    Mono<Boolean> existsByIdentification(String identification);
}
