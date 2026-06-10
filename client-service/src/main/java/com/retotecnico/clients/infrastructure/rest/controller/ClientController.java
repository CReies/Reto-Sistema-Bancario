package com.retotecnico.clients.infrastructure.rest.controller;

import com.retotecnico.clients.application.dto.ClientRequest;
import com.retotecnico.clients.application.dto.ClientResponse;
import com.retotecnico.clients.application.service.client.CreateClientService;
import com.retotecnico.clients.application.service.client.DeleteClientService;
import com.retotecnico.clients.application.service.client.FindAllClientService;
import com.retotecnico.clients.application.service.client.FindClientByIdService;
import com.retotecnico.clients.application.service.client.UpdateClientService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final FindAllClientService findAllClientService;
    private final FindClientByIdService findClientByIdService;
    private final CreateClientService createClientService;
    private final UpdateClientService updateClientService;
    private final DeleteClientService deleteClientService;

    @GetMapping
    public Flux<ClientResponse> findAll() {
        return findAllClientService.execute();
    }

    @GetMapping("/{id}")
    public Mono<ClientResponse> findById(@PathVariable Long id) {
        return findClientByIdService.execute(id);
    }

    @PostMapping
    public Mono<ResponseEntity<ClientResponse>> create(@Valid @RequestBody ClientRequest request) {
        return createClientService.execute(request)
                .map(response -> ResponseEntity.created(URI.create("/api/clients/" + response.id())).body(response));
    }

    @PutMapping("/{id}")
    public Mono<ClientResponse> update(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return updateClientService.execute(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return deleteClientService.execute(id).thenReturn(ResponseEntity.noContent().build());
    }
}
