# Report Gateway Service

## Descrição
O **Report Gateway Service** é um API Gateway para orquestração da geração de relatórios financeiros. Ele atua como um intermediário entre os clientes e os microserviços de relatórios, fornecendo funcionalidades como solicitação de relatórios, integração com serviços de pagamento, registro de transações e resiliência através de **Resilience4J**.

O projeto é baseado em **Spring Boot 3.4.2** e utiliza **Spring Cloud OpenFeign** para consumir APIs externas, **RabbitMQ** para processamento de pagamentos assíncronos e **PostgreSQL** como banco de dados relacional.

## Tecnologias Utilizadas
- **Java 21**
- **Spring Boot 3.4.2**
- **Spring Cloud OpenFeign** (para comunicação entre serviços)
- **Spring Data JPA** (para persistência)
- **PostgreSQL** (banco de dados relacional)
- **RabbitMQ** (mensageria para processar pagamentos)
- **Resilience4J** (circuit breaker e retry para resilência)
- **Spring Boot Actuator** (monitoramento)
- **Springdoc OpenAPI** (documentação da API via Swagger)
- **Docker e Docker Compose** (para containerização)

---
## Estrutura do Projeto
Abaixo está a estrutura principal do código:

```
report-gateway-service/
├── src/main/java/br/com/getnet/reportgatewayservice/
│   ├── controller/           # Controllers da API
│   ├── service/              # Camada de serviços
│   ├── repository/           # Repositórios do JPA
│   ├── model/                # Modelos do banco de dados
│   │   ├── domain/           # Entidades
│   │   ├── dto/              # DTOs de requisição e resposta
│   │   ├── enums/            # Enumerações
│   ├── config/               # Configurações do Spring Boot
│   ├── service/client/       # Feign Clients para consumir APIs externas
│   ├── exception/            # Classes de exceções personalizadas
│   ├── util/                 # Classes utilitárias
│   ├── ReportGatewayServiceApplication.java  # Classe principal da aplicação
├── src/main/resources/
│   ├── application.yml       # Configurações da aplicação
│   ├── logback.xml           # Configuração de logs
├── Dockerfile                # Dockerfile para containerização
├── docker-compose.yml        # Arquivo de orquestração de containers
├── pom.xml                   # Arquivo de dependências do Maven
```

---
## Executando o Projeto

### 1. Configuração de Banco de Dados
O projeto utiliza **PostgreSQL**. Se desejar rodar o banco de dados localmente, configure no `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/report_db
    username: postgres
    password: passw@rd
    driver-class-name: org.postgresql.Driver
```
Alternativamente, utilize o **Docker Compose** para subir o ambiente completo.

### 2. Executando com Docker Compose
Se você deseja rodar toda a aplicação junto com as dependências (**PostgreSQL e RabbitMQ**), basta rodar:
```sh
docker-compose up --build
```
Isso iniciará os containers:
- **PostgreSQL** na porta `5432`
- **RabbitMQ** na porta `5672` (gerenciamento em `http://localhost:15672`)
- **Report Gateway Service** na porta `8080`

### 3. Executando localmente (sem Docker)
Caso prefira rodar a aplicação diretamente no Maven:
```sh
mvn clean package -DskipTests
java -jar target/report-gateway-service-0.0.1-SNAPSHOT.jar
```

A API ficará acessível em `http://localhost:8080/api/v1/reports`.

### 4. Documentação da API
A documentação **Swagger** pode ser acessada em:
```
http://localhost:8080/swagger-ui.html
```

---
## Principais Endpoints

### 1. Criar um Relatório
**Endpoint:** `POST /api/v1/reports`

**Request Body:**
```json
{
  "cpf": "12345678901",
  "type": "BASIC",
  "amount": 5.00
}
```

**Response:**
```json
{
  "name": "João da Silva",
  "gender": "M",
  "nationality": "Brasileiro",
  "address": "Rua X, 123",
  "phone": "(11) 99999-9999",
  "document": "RG: 12345678"
}
```

### 2. Registrar Pagamento
**Endpoint:** `POST /api/v1/payments/register`

**Parâmetros:**
- `cpf`: CPF do cliente
- `amount`: Valor do pagamento

**Exemplo de Chamada:**
```sh
curl -X POST "http://localhost:8080/api/v1/payments/register?cpf=12345678901&amount=10.00"
```

### 3. Reverter Pagamento
**Endpoint:** `POST /api/v1/payments/rollback`

**Parâmetros:**
- `cpf`: CPF do cliente

**Exemplo de Chamada:**
```sh
curl -X POST "http://localhost:8080/api/v1/payments/rollback?cpf=12345678901"
```

---
## Resiliência e Tolerância a Falhas
A API utiliza **Resilience4J** para garantir que os serviços externos sejam consumidos de forma resiliente. As seguintes configurações estão habilitadas:

- **Circuit Breaker**: Se um serviço falhar repetidamente, as requisições são bloqueadas por um período.
- **Retry**: Tenta novas requisições caso a primeira falhe.

As configurações estão em `application.yml`:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      basic-report-service:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
      full-report-service:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
```



