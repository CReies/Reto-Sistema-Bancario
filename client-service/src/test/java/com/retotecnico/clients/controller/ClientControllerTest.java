package com.retotecnico.clients.infrastructure.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retotecnico.clients.application.dto.ClientRequest;
import com.retotecnico.clients.application.dto.ClientResponse;
import com.retotecnico.clients.application.service.client.CreateClientService;
import com.retotecnico.clients.application.service.client.DeleteClientService;
import com.retotecnico.clients.application.service.client.FindAllClientService;
import com.retotecnico.clients.application.service.client.FindClientByIdService;
import com.retotecnico.clients.application.service.client.UpdateClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindAllClientService findAllClientService;

    @MockBean
    private FindClientByIdService findClientByIdService;

    @MockBean
    private CreateClientService createClientService;

    @MockBean
    private UpdateClientService updateClientService;

    @MockBean
    private DeleteClientService deleteClientService;

    @Test
    void createReturnsCreated() throws Exception {
        var request = new ClientRequest("CLI-1", "Jose Lema", "Male", 35,
                "1002003004", "Otavalo sn y principal", "098254785", "1234", true);
        var response = new ClientResponse(1L, "CLI-1", "Jose Lema", "Male", 35,
                "1002003004", "Otavalo sn y principal", "098254785", true);
        when(createClientService.execute(any())).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.clientId").isEqualTo("CLI-1");
    }

    @Test
    void createValidatesRequiredFields() throws Exception {
        var request = new ClientRequest("", "", "Male", -1,
                "", "Dir", "098254785", "1234", true);

        webTestClient.post()
                .uri("/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid request");
    }
}
