package com.retotecnico.clients.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "clients")
public class Client extends Person {

    @Id
    private Long id;

    @Column("client_id")
    private String clientId;

    private String password;

    private Boolean active;
}
