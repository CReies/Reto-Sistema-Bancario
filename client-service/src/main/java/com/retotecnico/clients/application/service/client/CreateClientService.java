package com.retotecnico.clients.application.service.client;

import com.retotecnico.clients.application.dto.ClientRequest;
import com.retotecnico.clients.application.dto.ClientResponse;
import com.retotecnico.clients.application.mapper.ClientMapper;
import com.retotecnico.clients.application.port.out.ClientEventPublisherPort;
import com.retotecnico.clients.application.port.out.ClientRepositoryPort;
import com.retotecnico.clients.domain.exception.BusinessException;
import com.retotecnico.clients.domain.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateClientService {

    private final ClientRepositoryPort repository;
    private final ClientEventPublisherPort eventPublisher;
    private final ClientMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Mono<ClientResponse> execute(ClientRequest request) {
        return validateUnique(request)
                .then(Mono.defer(() -> {
                    Client client = mapper.toEntity(request);
                    client.setPassword(passwordEncoder.encode(request.password()));
                    return repository.save(client);
                }))
                .flatMap(saved -> eventPublisher.publish(mapper.toEvent("CREATED", saved)).thenReturn(saved))
                .map(mapper::toResponse);
    }

    private Mono<Void> validateUnique(ClientRequest request) {
        return repository.existsByClientId(request.clientId())
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException("The clientId already exists"))
                        : repository.existsByIdentification(request.identification()))
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException("The identification already exists"))
                        : Mono.empty());
    }
}
