package com.retotecnico.clients.application.service.client;

import com.retotecnico.clients.application.dto.ClientRequest;
import com.retotecnico.clients.application.dto.ClientResponse;
import com.retotecnico.clients.application.mapper.ClientMapper;
import com.retotecnico.clients.application.port.out.ClientEventPublisherPort;
import com.retotecnico.clients.application.port.out.ClientRepositoryPort;
import com.retotecnico.clients.domain.exception.BusinessException;
import com.retotecnico.clients.domain.exception.ResourceNotFoundException;
import com.retotecnico.clients.domain.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpdateClientService {

    private final ClientRepositoryPort repository;
    private final ClientEventPublisherPort eventPublisher;
    private final ClientMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Mono<ClientResponse> execute(Long id, ClientRequest request) {
        return findClient(id)
                .flatMap(client -> validateUnique(client, request).thenReturn(client))
                .flatMap(client -> {
                    mapper.updateEntity(client, request);
                    client.setPassword(passwordEncoder.encode(request.password()));
                    return repository.save(client);
                })
                .flatMap(saved -> eventPublisher.publish(mapper.toEvent("UPDATED", saved)).thenReturn(saved))
                .map(mapper::toResponse);
    }

    private Mono<Client> findClient(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Client not found: " + id)));
    }

    private Mono<Void> validateUnique(Client client, ClientRequest request) {
        Mono<Boolean> duplicatedClientId = client.getClientId().equals(request.clientId())
                ? Mono.just(false)
                : repository.existsByClientId(request.clientId());
        Mono<Boolean> duplicatedIdentification = client.getIdentification().equals(request.identification())
                ? Mono.just(false)
                : repository.existsByIdentification(request.identification());

        return duplicatedClientId
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException("The clientId already exists"))
                        : duplicatedIdentification)
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException("The identification already exists"))
                        : Mono.empty());
    }
}
