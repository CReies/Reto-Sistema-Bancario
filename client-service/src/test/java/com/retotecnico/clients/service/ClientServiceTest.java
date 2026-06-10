package com.retotecnico.clients.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.retotecnico.clients.application.dto.ClientRequest;
import com.retotecnico.clients.application.mapper.ClientMapper;
import com.retotecnico.clients.application.service.client.CreateClientService;
import com.retotecnico.clients.application.service.client.FindClientByIdService;
import com.retotecnico.clients.domain.model.Client;
import com.retotecnico.clients.domain.exception.BusinessException;
import com.retotecnico.clients.application.port.out.ClientEventPublisherPort;
import com.retotecnico.clients.application.port.out.ClientRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepositoryPort repository;

    @Mock
    private ClientEventPublisherPort eventPublisher;

    private PasswordEncoder passwordEncoder;

    private CreateClientService createClientService;

    private FindClientByIdService findClientByIdService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        var mapper = Mappers.getMapper(ClientMapper.class);
        createClientService = new CreateClientService(
                repository,
                eventPublisher,
                mapper,
                passwordEncoder
        );
        findClientByIdService = new FindClientByIdService(repository, mapper);
    }

    @Test
    void createRejectsDuplicatedClientId() {
        ClientRequest request = validRequest();
        when(repository.existsByClientId("CLI-1")).thenReturn(Mono.just(true));

        StepVerifier.create(createClientService.execute(request))
                .expectErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(BusinessException.class)
                        .hasMessage("The clientId already exists"))
                .verify();
    }

    @Test
    void findByIdReturnsClientWithoutPassword() {
        Client client = client();
        when(repository.findById(1L)).thenReturn(Mono.just(client));

        StepVerifier.create(findClientByIdService.execute(1L))
                .assertNext(response -> {
                    assertThat(response.clientId()).isEqualTo("CLI-1");
                    assertThat(response.name()).isEqualTo("Jose Lema");
                })
                .verifyComplete();
        verify(repository).findById(1L);
    }

    @Test
    void createStoresEncodedPassword() {
        ClientRequest request = validRequest();
        when(repository.existsByClientId("CLI-1")).thenReturn(Mono.just(false));
        when(repository.existsByIdentification("1002003004")).thenReturn(Mono.just(false));
        when(repository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(eventPublisher.publish(any())).thenReturn(Mono.empty());

        StepVerifier.create(createClientService.execute(request))
                .expectNextCount(1)
                .verifyComplete();

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(repository).save(captor.capture());
        String storedPassword = captor.getValue().getPassword();
        assertThat(storedPassword).isNotEqualTo("1234");
        assertThat(passwordEncoder.matches("1234", storedPassword)).isTrue();
    }

    private ClientRequest validRequest() {
        return new ClientRequest("CLI-1", "Jose Lema", "Male", 35,
                "1002003004", "Otavalo sn y principal", "098254785", "1234", true);
    }

    private Client client() {
        Client client = new Client();
        client.setId(1L);
        client.setClientId("CLI-1");
        client.setName("Jose Lema");
        client.setGender("Male");
        client.setAge(35);
        client.setIdentification("1002003004");
        client.setAddress("Otavalo sn y principal");
        client.setPhone("098254785");
        client.setPassword("1234");
        client.setActive(true);
        return client;
    }
}
