package com.retotecnico.accounts.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "client_projections")
public class ClientProjection implements Persistable<String> {

    @Id
    @Column("client_id")
    private String clientId;

    private String name;

    private String identification;

    private Boolean active;

    @Transient
    private boolean newProjection = true;

    @PersistenceCreator
    public ClientProjection(String clientId, String name, String identification, Boolean active) {
        this.clientId = clientId;
        this.name = name;
        this.identification = identification;
        this.active = active;
        this.newProjection = false;
    }

    @Override
    @Transient
    public String getId() {
        return clientId;
    }

    @Override
    @Transient
    public boolean isNew() {
        return newProjection;
    }
}
