### **ECOSSISTEMA DE GERAÇÃO DE RELATÓRIOS PARA PESSOA FÍSICA**

#### **Descrição**
O objetivo deste épico é desenvolver um ecossistema distribuído para geração de relatórios financeiros sobre pessoas físicas. O sistema deverá ser capaz de processar solicitações de relatórios básicos e completos, garantindo a correta cobrança dos valores, a integridade dos dados e a alta disponibilidade dos serviços.

O sistema será composto por **quatro aplicações independentes** que se comunicam entre si via REST e mensageria (RabbitMQ). Além disso, deverá garantir resiliência em caso de falhas e ser implementado seguindo boas práticas de microsserviços, incluindo escalabilidade, testes unitários, e controle de transações com rollback.

---

### **Justificativa**
Este épico foi definido para atender às necessidades do cliente, fornecendo um sistema modular e distribuído para geração de relatórios com alto desempenho, segurança e resiliência. A arquitetura baseada em microsserviços permite escalabilidade e facilita a manutenção a longo prazo.

Dada a necessidade de cobrança pelos relatórios, é essencial que o sistema mantenha um controle financeiro preciso e implemente rollback adequado em caso de falhas. Além disso, como o relatório completo depende da execução simultânea de dois serviços, a solução deve garantir a execução assíncrona e a consolidação dos resultados.

---

### **Requisitos**

#### **Funcionais**
1. O sistema deve permitir que o usuário solicite um relatório informando CPF e tipo (básico ou completo).
2. O relatório básico deve conter Nome, Sexo e Nacionalidade.
3. O relatório completo deve conter todas as informações do básico, além de Endereço, Telefone e Documentos (RG e CPF).
4. O sistema deve validar o CPF e, caso a soma dos números seja **igual a 44**, o relatório não poderá ser exibido.
5. O sistema deve cobrar **R$5,00** pelo relatório básico e **R$10,00** pelo relatório completo.
6. O valor da cobrança deve ser salvo no banco de dados antes da geração do relatório.
7. O relatório completo deve ser gerado de forma assíncrona, consolidando as informações dos microsserviços responsáveis por Relatório Básico e Relatório Completo.
8. Caso um dos microsserviços de relatório falhe, o sistema deve tentar novamente **duas vezes** com um intervalo de **300ms** entre as tentativas.
9. Caso a geração do relatório completo falhe mesmo após as tentativas, o sistema deve realizar rollback da cobrança do relatório completo e cobrar apenas o valor do relatório básico.
10. O serviço financeiro deve processar a cobrança de forma assíncrona utilizando RabbitMQ e Spring Cloud Stream.
11. O sistema deve responder todas as requisições no formato JSON via REST.
12. A aplicação deve ser documentada via Swagger.

#### **Não Funcionais**
1. As aplicações devem ser desenvolvidas em **Java 17+** utilizando **Spring Boot 3.4.2**.
2. Deve ser utilizado **banco de dados relacional** no serviço de entrada para registrar as cobranças.
3. A arquitetura deve seguir o padrão de **microsserviços**.
4. Comunicação entre serviços via **REST** e **RabbitMQ**.
5. Implementação de **resiliência** e **controle de transações** com rollback.
6. Testes unitários obrigatórios utilizando **JUnit 5 e Mockito**.
7. Implementação opcional de **Docker e Docker Compose** para deploy local.
8. Utilização opcional de **Swagger** para documentação.
9. Desenho da arquitetura do ecossistema.

---

### **Histórias de Usuário**
A seguir, detalhamos as histórias de usuário organizadas por cada um dos quatro aplicativos.

#### **📌 Aplicação 1: Entrada (API Gateway e Orquestrador)**
- **Como usuário, quero solicitar a geração de um relatório informando meu CPF e o tipo de relatório (básico ou completo)**
- **Como sistema, quero validar se o CPF informado possui soma dos dígitos igual a 44 para restringir a exibição do relatório**
- **Como sistema, quero salvar no banco de dados a cobrança do relatório solicitado antes da geração**
- **Como sistema, quero garantir que a requisição de relatório completo execute os serviços necessários de forma assíncrona**
- **Como sistema, quero consolidar os dados retornados pelos microsserviços de relatório e devolver um JSON único ao usuário**
- **Como sistema, quero garantir rollback da cobrança do relatório completo caso a geração falhe e cobrar apenas o relatório básico**

#### **📌 Aplicação 2: Relatório Básico**
- **Como sistema, quero fornecer informações públicas de um CPF informado, incluindo Nome, Sexo e Nacionalidade**
- **Como sistema, quero expor um endpoint REST que permita o consumo da API pelo serviço de entrada**

#### **📌 Aplicação 3: Relatório Completo**
- **Como sistema, quero fornecer informações detalhadas de um CPF, incluindo Endereço, Telefone e Documentos (RG e CPF)**
- **Como sistema, quero garantir que minha execução seja feita de forma assíncrona e permitir a reexecução caso falhe**
- **Como sistema, quero expor um endpoint REST que permita o consumo da API pelo serviço de entrada**

#### **📌 Aplicação 4: Financeiro**
- **Como sistema, quero processar as cobranças dos relatórios utilizando RabbitMQ**
- **Como sistema, quero escutar mensagens de cobrança enviadas pelo serviço de entrada**
- **Como sistema, quero registrar cobranças bem-sucedidas e falhas para auditoria**
- **Como sistema, quero garantir que o rollback da cobrança seja processado corretamente em caso de falha**

---

### **Backlog das Tarefas SCRUM**
Aqui estão todas as **tarefas** organizadas em **sprints**, priorizando as mais críticas primeiro.

#### **Sprint 1: Configuração inicial e estruturação**
1. Criar estrutura do projeto e repositórios
2. Configurar aplicações Spring Boot 3.4.2 com Java 17
3. Criar estrutura de banco de dados relacional no serviço de entrada
4. Implementar comunicação entre serviços via REST
5. Configurar mensageria RabbitMQ no serviço financeiro
6. Criar testes unitários iniciais para as classes de domínio

#### **Sprint 2: Implementação das funcionalidades principais**
7. Implementar serviço de Entrada (API Gateway)
8. Implementar serviço de Relatório Básico
9. Implementar serviço de Relatório Completo
10. Implementar serviço Financeiro com RabbitMQ
11. Criar testes unitários para validação de CPF e lógica de restrição
12. Criar testes unitários para rollback da cobrança

#### **Sprint 3: Resiliência, integração e testes finais**
13. Implementar mecanismo de reexecução em caso de falha nos relatórios (retry)
14. Implementar rollback automático em caso de falha total na geração do relatório completo
15. Criar documentação Swagger para APIs
16. Implementar Docker e Docker Compose (opcional)
17. Criar desenho da arquitetura do ecossistema
18. Realizar testes integrados de ponta a ponta
19. Validar performance e comportamento em cenários de erro

---