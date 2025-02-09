# Geração de Relatórios para Pessoa Física

## Visão Geral
O objetivo deste épico é desenvolver um ecossistema que permita a geração de relatórios para pessoa física a partir de um endpoint. O sistema irá classificar o tipo de relatório (Básico ou Completo), calcular o valor a ser cobrado e consolidar os dados para entrega ao usuário final. Também será implementado um mecanismo de rollback e fallback caso haja falhas na geração do relatório completo.

## Requisitos do Negócio
- O cliente pode solicitar um **Relatório Básico** (R$ 5,00) ou um **Relatório Completo** (R$ 10,00);
- O Relatório Básico retorna: Nome, Sexo e Nacionalidade;
- O Relatório Completo retorna tudo do Básico + Endereço, Telefone e Documentos (RG e CPF);
- Se a soma dos dígitos do CPF for igual a **44**, a geração do relatório será negada.

## Requisitos Técnicos
- Desenvolvimento de **4 aplicativos** em **Java 17+** utilizando **Spring Boot 3.4.2**;
- **App 1 (Entrada):**
  - Deve utilizar obrigatoriamente banco de dados relacional;
  - O valor a cobrar deve ser gravado no banco antes da geração do relatório;
  - A resposta da API deve incluir o valor cobrado;
  - Implementa a lógica de fallback para cobrar o básico caso o completo falhe;
  - Retorna a resposta final ao cliente.
- **App 2 (Relatório Básico):**
  - Expondo endpoint REST para fornecer dados públicos do relatório básico;
- **App 3 (Relatório Completo):**
  - Expondo endpoint REST para fornecer os dados complementares do relatório completo;
- **App 4 (Financeiro):**
  - Deve utilizar **RabbitMQ** via **Spring Cloud Stream** para comunicação assíncrona;
- Execução do **App 2 e App 3 de forma assíncrona** ao solicitar relatório completo;
- Repetir a tentativa de requisição ao App 3 **duas vezes com delay de 300ms** antes de acionar o fallback;
- Rollback do valor cobrado caso o relatório completo falhe;
- **Testes unitários** utilizando **JUnit 5 e Mockito**;
- Opcional: **Docker, Docker Compose, Swagger, Desenho de Arquitetura**.

---

## Backlog de Atividades (Scrum)

### Histórias de Usuário
#### **US-001 - Como cliente, desejo solicitar um relatório básico**
**Critérios de aceitação:**
- O endpoint deve permitir a escolha do relatório básico;
- O sistema deve validar o CPF do usuário;
- O preço de R$ 5,00 deve ser armazenado no banco de dados;
- O endpoint deve retornar Nome, Sexo e Nacionalidade;

#### **US-002 - Como cliente, desejo solicitar um relatório completo**
**Critérios de aceitação:**
- O endpoint deve permitir a escolha do relatório completo;
- O sistema deve validar o CPF do usuário;
- O preço de R$ 10,00 deve ser armazenado no banco de dados;
- Os Apps 2 e 3 devem ser chamados de forma assíncrona;
- O resultado deve ser consolidado e retornado ao cliente;
- Em caso de falha do App 3, deve-se tentar duas novas requisições com intervalo de 300ms;
- Se falhar definitivamente, o sistema deve reverter a cobrança e processar apenas o básico.

#### **US-003 - Como cliente, desejo ser informado caso meu CPF esteja restrito**
**Critérios de aceitação:**
- O sistema deve somar os dígitos do CPF;
- Se a soma for 44, deve bloquear a geração do relatório;
- O cliente deve ser informado da impossibilidade de emissão do relatório.

---

## BDD - Comportamento do Sistema

### **Cenário 1: Geração de Relatório Básico**
```
Dado que um cliente solicita um relatório básico
Quando a API recebe o pedido e valida os dados
Então o sistema armazena o valor R$ 5,00 no banco de dados
E retorna Nome, Sexo e Nacionalidade ao cliente.
```

### **Cenário 2: Geração de Relatório Completo**
```
Dado que um cliente solicita um relatório completo
Quando a API recebe o pedido e valida os dados
Então o sistema armazena o valor R$ 10,00 no banco de dados
E chama os Apps 2 e 3 de forma assíncrona
E consolida os resultados antes de retornar ao cliente.
```

### **Cenário 3: Rollback em Falha**
```
Dado que um cliente solicita um relatório completo
E o App 3 falha na resposta
Quando o sistema tenta duas novas requisições com 300ms de intervalo
E ambas falham
Então o sistema reverte a cobrança e processa apenas o básico.
```

### **Cenário 4: CPF Restrito**
```
Dado que um cliente solicita um relatório
Quando a soma dos dígitos do CPF for 44
Então o sistema bloqueia a emissão do relatório
E informa o cliente sobre a restrição.
```

---

## Definition of Done (DoD)
- Todo código deve estar versionado no repositório oficial;
- Todas as histórias de usuário devem possuir testes unitários;
- As falhas devem ser tratadas corretamente (rollback, fallback e exceções);
- A documentação deve estar atualizada no Swagger;
- Nenhuma história pode ser marcada como "Concluída" sem revisão de código.

---

Para iniciar o desenvolvimento do projeto de **Geração de Relatórios para Pessoa Física**, os próximos passos seguem uma abordagem estruturada de arquitetura e implementação:

---

## **1. Preparação do Ambiente e Ferramentas (1-2 dias)**
- [ ] Criar o repositório Git para versionamento do código.
- [ ] Configurar o ambiente de desenvolvimento:
  - **Java 17** (ou superior)
  - **Spring Boot 3.4.2**
  - **Maven ou Gradle**
  - **Banco de Dados Relacional** (MySQL, PostgreSQL ou H2 para desenvolvimento)
  - **Ferramentas de comunicação assíncrona** (RabbitMQ)
- [ ] Definir a stack de ferramentas de suporte:
  - **Docker e Docker Compose** (para padronização do ambiente)
  - **Swagger/OpenAPI** para documentação da API.
  - **Lombok e MapStruct** para facilitar a codificação.

---

## **2. Definição da Arquitetura do Sistema (2-3 dias)**
- [ ] **Diagrama da Arquitetura:** Criar um desenho de alto nível, detalhando como os 4 aplicativos interagem entre si.
- [ ] **Modelo de Dados:**
  - Definir o esquema do banco de dados para armazenar as cobranças e logs das requisições.
- [ ] **Definir o Protocolo de Comunicação:**
  - REST para comunicação entre os serviços.
  - Mensageria com **RabbitMQ** para processar eventos financeiros.
- [ ] **Definir o Fluxo de Requisição:**
  - Como o App 1 gerencia as chamadas assíncronas para os Apps 2 e 3.
  - Como o App 4 trata as transações financeiras e o rollback.
- [ ] **Definir padrões de logs e monitoramento:**
  - Utilização de **Spring Boot Actuator** e logs estruturados para depuração.

---

## **3. Implementação Inicial da Infraestrutura (2-4 dias)**
- [ ] Criar a estrutura base do projeto com pacotes organizados:
  - `com.projeto.app1.entrada`
  - `com.projeto.app2.relatoriobasico`
  - `com.projeto.app3.relatoriocompleto`
  - `com.projeto.app4.financeiro`
- [ ] Implementar configurações básicas do **Spring Boot**:
  - Configurar **Spring Security** (se necessário).
  - Configurar **Spring Data JPA** e conexão com o banco de dados.
  - Configurar **RabbitMQ** no **App 4** para processar mensagens financeiras.
- [ ] Criar os **endpoints iniciais** para cada aplicação, retornando respostas mockadas.

---

## **4. Desenvolvimento das Funcionalidades (1-2 semanas)**
### **App 1 - Entrada**
- [ ] Criar a API REST para receber solicitações de relatórios.
- [ ] Implementar a validação do CPF e do tipo de relatório.
- [ ] Armazenar o valor no banco de dados antes da solicitação.
- [ ] Implementar chamadas assíncronas para os Apps 2 e 3.
- [ ] Implementar lógica de rollback em caso de falha.

### **App 2 - Relatório Básico**
- [ ] Criar API REST para fornecer informações básicas (Nome, Sexo, Nacionalidade).
- [ ] Implementar persistência no banco de dados.

### **App 3 - Relatório Completo**
- [ ] Criar API REST para fornecer dados adicionais (Endereço, Telefone, Documentos).
- [ ] Implementar integração com o App 2.
- [ ] Implementar lógica para realizar duas tentativas em caso de falha.

### **App 4 - Financeiro**
- [ ] Criar sistema de mensageria via **RabbitMQ**.
- [ ] Implementar fila para processar transações financeiras.
- [ ] Criar lógica de compensação para rollback de cobrança.

---

## **5. Testes e Validação (1 semana)**
- [ ] Implementar **testes unitários** usando **JUnit 5** e **Mockito**.
- [ ] Implementar **testes de integração** para validar a comunicação entre os serviços.
- [ ] Criar testes de carga para verificar a escalabilidade das APIs.

---

## **6. Deploy e Documentação (2-3 dias)**
- [ ] Criar **Dockerfile** para cada aplicação.
- [ ] Configurar **Docker Compose** para facilitar a orquestração.
- [ ] Gerar a documentação com **Swagger/OpenAPI**.
- [ ] Criar um **README.md** explicando como rodar o projeto.

---

## **Conclusão**
Esses passos garantem que o desenvolvimento siga uma abordagem bem estruturada e eficiente, minimizando riscos e facilitando a manutenção do sistema. 🚀  

Caso precise de ajustes ou priorização em alguma área, me avise!

