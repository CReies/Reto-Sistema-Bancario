# Client Management Microservice

`client-service` manages client information for the banking system. It exposes CRUD operations for clients and publishes asynchronous client events so other services, such as `account-service`, can keep their own local projection.

## Responsibilities

- Create, retrieve, update, and delete clients.
- Validate person and client data before persistence.
- Persist data reactively in an independent PostgreSQL database with R2DBC.
- Publish client events through RabbitMQ with Reactor RabbitMQ.

## Environment Variables

| Variable | Default | Description |
| --- | --- | --- |
| `SERVER_PORT` | `8081` | HTTP port. |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5433/clients_db` | PostgreSQL JDBC URL used by Flyway migrations. |
| `SPRING_DATASOURCE_USERNAME` | `clients_user` | Flyway database user. |
| `SPRING_DATASOURCE_PASSWORD` | `clients_pass` | Flyway database password. |
| `SPRING_R2DBC_URL` | `r2dbc:postgresql://localhost:5433/clients_db` | PostgreSQL R2DBC URL used by the application. |
| `SPRING_R2DBC_USERNAME` | `clients_user` | R2DBC database user. |
| `SPRING_R2DBC_PASSWORD` | `clients_pass` | R2DBC database password. |
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

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/clients` | Lists all registered clients. |
| `GET` | `/api/clients/{id}` | Gets a client by identifier. |
| `POST` | `/api/clients` | Creates a new client. |
| `PUT` | `/api/clients/{id}` | Updates an existing client. |
| `DELETE` | `/api/clients/{id}` | Deletes a client. |

## Example Request

```http
POST /api/clients
Content-Type: application/json
```

```json
{
  "clientId": "CLI-1",
  "name": "Jose Lema",
  "gender": "Male",
  "age": 35,
  "identification": "CLI-1",
  "address": "Otavalo sn y principal",
  "phone": "0999999999",
  "password": "1234",
  "active": true
}
```

## Internal Structure

```text
src/main/java/com/retotecnico/clients/
|-- domain
|-- application
`-- infrastructure
```

`Client` extends `Person` as a domain base class. Relevant client changes are published as JSON events to decouple integration with `account-service`.
