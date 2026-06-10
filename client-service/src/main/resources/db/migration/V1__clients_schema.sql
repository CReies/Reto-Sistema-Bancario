create table clients (
    id bigserial primary key,
    client_id varchar(50) not null unique,
    name varchar(120) not null,
    gender varchar(30) not null,
    age integer not null,
    identification varchar(40) not null unique,
    address varchar(180) not null,
    phone varchar(40) not null,
    password varchar(120) not null,
    active boolean not null
);
