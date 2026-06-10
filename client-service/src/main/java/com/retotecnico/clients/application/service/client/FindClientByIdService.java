package com.retotecnico.clients.application.service.client;

import com.retotecnico.clients.application.dto.ClientResponse;
import com.retotecnico.clients.application.mapper.ClientMapper;
import com.retotecnico.clients.application.port.out.ClientRepositoryPort;
import com.retotecnico.clients.domain.exception.ResourceNotFoundException;
import com.retotecnico.clients.domain.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FindClientByIdService {

    private final ClientRepositoryPort repository;
    private final ClientMapper mapper;

    @Transactional(readOnly = true)
    public Mono<ClientResponse> execute(Long id) {
        return findClient(id).map(mapper::toResponse);
    }

    private Mono<Client> findClient(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Client not found: " + id)));
    }
}
