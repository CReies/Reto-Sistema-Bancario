# Banking Backend with Microservices

Backend system that simulates basic banking operations through two independent microservices: client management and bank account management. The project includes CRUD operations, transaction registration, deposits, withdrawals, and available-balance validation before debit operations.

## Architecture

The solution is organized as a microservices architecture with separate responsibilities:

- `client-service`: manages people and clients.
- `account-service`: manages bank accounts, transactions, deposits, withdrawals, and reports.
- PostgreSQL: one independent database per microservice.
- RabbitMQ: asynchronous communication between services.

## Technology Stack

| Tool | Purpose |
| --- | --- |
| Java 21 | Main runtime |
| Spring Boot WebFlux | Reactive REST API and validation |
| Spring Data R2DBC | Reactive PostgreSQL persistence |
| Reactor RabbitMQ | Reactive RabbitMQ publishing and consuming |
| Gradle | Build, tests, and dependency management |
| PostgreSQL | Relational persistence |
| RabbitMQ | Event-driven integration |
| Flyway | Database migrations |
| Podman Compose | Local infrastructure |

## Project Structure

```text
.
|-- client-service/
|   |-- src/main/java/com/retotecnico/clients/
|   |-- src/main/resources/db/migration/
|   |-- src/test/java/
|   |-- build.gradle
|   |-- Containerfile
|   `-- README.md
|-- account-service/
|   |-- src/main/java/com/retotecnico/accounts/
|   |-- src/main/resources/db/migration/
|   |-- src/test/java/
|   |-- build.gradle
|   |-- Containerfile
|   `-- README.md
|-- docs/postman_collection.json
|-- BaseDatos.sql
|-- podman-compose.yml
`-- README.md
```

## Local Build

From the repository root, build each microservice:

```bash
cd client-service
./gradlew clean test

cd ../account-service
./gradlew clean test
```

On Windows:

```powershell
cd client-service
.\gradlew.bat clean test

cd ..\account-service
.\gradlew.bat clean test
```

## Run with Podman Compose

The `podman-compose.yml` file builds and runs:

- the clients database,
- the accounts database,
- RabbitMQ,
- `client-service` on port `8081`,
- `account-service` on port `8082`.

```bash
podman compose up --build
```

If `podman compose` is unavailable on Windows, use:

```powershell
podman-compose up --build
```

## Base URLs

```text
Client Service:  http://localhost:8081/api
Account Service: http://localhost:8082/api
```

## API Summary

### Clients

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/clients` | Lists all registered clients. |
| `GET` | `/api/clients/{id}` | Gets a client by identifier. |
| `POST` | `/api/clients` | Creates a new client. |
| `PUT` | `/api/clients/{id}` | Updates an existing client. |
| `DELETE` | `/api/clients/{id}` | Deletes a client. |

### Accounts and Transactions

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/accounts` | Lists all bank accounts. |
| `GET` | `/api/accounts/{id}` | Gets an account by identifier. |
| `POST` | `/api/accounts` | Creates a new bank account. |
| `PUT` | `/api/accounts/{id}` | Updates an existing account. |
| `DELETE` | `/api/accounts/{id}` | Deletes an account. |
| `GET` | `/api/transactions?accountNumber={accountNumber}` | Lists transactions for an account. |
| `GET` | `/api/transactions/{id}` | Gets a transaction by identifier. |
| `POST` | `/api/transactions` | Registers a deposit or withdrawal. |
| `PUT` | `/api/transactions/{id}` | Updates an existing transaction. |
| `DELETE` | `/api/transactions/{id}` | Deletes a transaction. |
| `GET` | `/api/reports?clientId={clientId}&from={startDate}&to={endDate}` | Generates an account statement by client and date range. |

## Postman

The Postman collection is available at:

```text
docs/postman_collection.json
```

Recommended flow:

1. Start the infrastructure and both services.
2. Import the collection into Postman.
3. Run `00 - Initialize variables`.
4. Execute the requests in order.

## Recommended Test Flow

1. Create clients from `client-service`.
2. Create linked accounts from `account-service`.
3. Register deposits.
4. Register withdrawals and validate the insufficient-balance rule.
5. Query transactions and reports.

## Microservice Documentation

Each microservice contains its own `README.md` with service responsibilities, local configuration, environment variables, endpoints, and internal structure:

- `client-service/README.md`
- `account-service/README.md`
