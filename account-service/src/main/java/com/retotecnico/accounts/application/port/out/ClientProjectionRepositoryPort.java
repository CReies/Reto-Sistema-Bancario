package com.retotecnico.accounts.application.port.out;

import com.retotecnico.accounts.domain.model.ClientProjection;
import reactor.core.publisher.Mono;

public interface ClientProjectionRepositoryPort {

    Mono<ClientProjection> findById(String clientId);

    <S extends ClientProjection> Mono<S> save(S clientProjection);

    Mono<Void> deleteById(String clientId);
}
