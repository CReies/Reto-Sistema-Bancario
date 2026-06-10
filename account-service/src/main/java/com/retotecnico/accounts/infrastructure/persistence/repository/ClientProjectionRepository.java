package com.retotecnico.accounts.infrastructure.persistence.repository;

import com.retotecnico.accounts.application.port.out.ClientProjectionRepositoryPort;
import com.retotecnico.accounts.domain.model.ClientProjection;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ClientProjectionRepository extends ReactiveCrudRepository<ClientProjection, String>, ClientProjectionRepositoryPort {
}
