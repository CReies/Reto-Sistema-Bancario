package com.retotecnico.clients.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Person {

    private String name;

    private String gender;

    private Integer age;

    private String identification;

    private String address;

    private String phone;
}
