package com.retotecnico.clients.application.service.client;

import com.retotecnico.clients.application.dto.ClientResponse;
import com.retotecnico.clients.application.mapper.ClientMapper;
import com.retotecnico.clients.application.port.out.ClientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class FindAllClientService {

    private final ClientRepositoryPort repository;
    private final ClientMapper mapper;

    @Transactional(readOnly = true)
    public Flux<ClientResponse> execute() {
        return repository.findAll().map(mapper::toResponse);
    }
}
