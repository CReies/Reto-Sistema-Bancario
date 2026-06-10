create database clients_db;
create database accounts_db;

\connect clients_db;

create table if not exists clients (
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

\connect accounts_db;

create table if not exists client_projections (
    client_id varchar(50) primary key,
    name varchar(120) not null,
    identification varchar(40) not null,
    active boolean not null
);

create table if not exists accounts (
    id bigserial primary key,
    account_number varchar(30) not null unique,
    account_type varchar(40) not null,
    initial_balance numeric(19, 2) not null,
    current_balance numeric(19, 2) not null,
    active boolean not null,
    client_id varchar(50) not null
);

create table if not exists transactions (
    id bigserial primary key,
    account_id bigint not null references accounts(id),
    date timestamp with time zone not null,
    transaction_type varchar(40) not null,
    amount numeric(19, 2) not null,
    balance numeric(19, 2) not null
);

create index if not exists idx_accounts_client_id on accounts(client_id);
create index if not exists idx_transactions_account_date on transactions(account_id, date);
