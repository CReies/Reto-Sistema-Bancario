create table client_projections (
    client_id varchar(50) primary key,
    name varchar(120) not null,
    identification varchar(40) not null,
    active boolean not null
);

create table accounts (
    id bigserial primary key,
    account_number varchar(30) not null unique,
    account_type varchar(40) not null,
    initial_balance numeric(19, 2) not null,
    current_balance numeric(19, 2) not null,
    active boolean not null,
    client_id varchar(50) not null
);

create index idx_accounts_client_id on accounts(client_id);

create table transactions (
    id bigserial primary key,
    account_id bigint not null references accounts(id),
    date timestamp with time zone not null,
    transaction_type varchar(40) not null,
    amount numeric(19, 2) not null,
    balance numeric(19, 2) not null
);

create index idx_transactions_account_date on transactions(account_id, date);
