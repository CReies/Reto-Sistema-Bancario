package com.retotecnico.clients.application.mapper;

import com.retotecnico.clients.application.dto.ClientRequest;
import com.retotecnico.clients.application.dto.ClientResponse;
import com.retotecnico.clients.domain.event.ClientChangedEvent;
import com.retotecnico.clients.domain.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    Client toEntity(ClientRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(@MappingTarget Client client, ClientRequest request);

    ClientResponse toResponse(Client client);

    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "id", source = "client.id")
    @Mapping(target = "clientId", source = "client.clientId")
    @Mapping(target = "name", source = "client.name")
    @Mapping(target = "identification", source = "client.identification")
    @Mapping(target = "active", source = "client.active")
    ClientChangedEvent toEvent(String eventType, Client client);
}
