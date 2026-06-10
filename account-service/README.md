# Account Management Microservice

`account-service` manages bank accounts, transactions, and account-statement reports. It implements deposits and withdrawals, including available-balance validation before debit operations.

## Responsibilities

- Create, retrieve, update, and delete bank accounts.
- Register account transactions.
- Execute deposits and withdrawals.
- Validate insufficient balance before withdrawals.
- Generate reports by client and date range.
- Consume client events from RabbitMQ with Reactor RabbitMQ to maintain a local client projection.

## Environment Variables

| Variable | Default | Description |
| --- | --- | --- |
| `SERVER_PORT` | `8082` | HTTP port. |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5434/accounts_db` | PostgreSQL JDBC URL used by Flyway migrations. |
| `SPRING_DATASOURCE_USERNAME` | `accounts_user` | Flyway database user. |
| `SPRING_DATASOURCE_PASSWORD` | `accounts_pass` | Flyway database password. |
| `SPRING_R2DBC_URL` | `r2dbc:postgresql://localhost:5434/accounts_db` | PostgreSQL R2DBC URL used by the application. |
| `SPRING_R2DBC_USERNAME` | `accounts_user` | R2DBC database user. |
| `SPRING_R2DBC_PASSWORD` | `accounts_pass` | R2DBC database password. |
| `SPRING_RABBITMQ_HOST` | `localhost` | RabbitMQ host. |
| `SPRING_RABBITMQ_PORT` | `5672` | RabbitMQ port. |

## Run Locally

```bash
./gradlew bootRun
```

On Windows:

```powershell
.\gradlew.bat bootRun
```

## Tests

```bash
./gradlew clean test
```

## Endpoints

Base path: `/api`

### Accounts

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/accounts` | Lists all bank accounts. |
| `GET` | `/api/accounts/{id}` | Gets an account by identifier. |
| `POST` | `/api/accounts` | Creates a new bank account. |
| `PUT` | `/api/accounts/{id}` | Updates an existing account. |
| `DELETE` | `/api/accounts/{id}` | Deletes an account. |

### Transactions

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/transactions?accountNumber={accountNumber}` | Lists transactions for an account. |
| `GET` | `/api/transactions/{id}` | Gets a transaction by identifier. |
| `POST` | `/api/transactions` | Registers a deposit or withdrawal. |
| `PUT` | `/api/transactions/{id}` | Updates an existing transaction. |
| `DELETE` | `/api/transactions/{id}` | Deletes a transaction. |

### Reports

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/reports?clientId={clientId}&from={startDate}&to={endDate}` | Generates an account statement by client and date range. |

## Example Account Request

```http
POST /api/accounts
Content-Type: application/json
```

```json
{
  "accountNumber": "478758",
  "accountType": "Savings",
  "initialBalance": 2000.00,
  "active": true,
  "clientId": "CLI-1"
}
```

## Example Transaction Request

```json
{
  "accountNumber": "478758",
  "transactionType": "DEPOSIT",
  "amount": 100.00
}
```

## Business Rules

- A withdrawal must not be registered if the account has insufficient balance.
- Transactions update the available account balance.
- Account creation depends on the client existing in the local projection received through messaging.

## Internal Structure

```text
src/main/java/com/retotecnico/accounts/
|-- domain
|-- application
`-- infrastructure
```

The service keeps a local client projection to validate account creation without synchronous HTTP calls. Balance validation is concentrated in the transaction use case.
