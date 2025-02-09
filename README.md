### **ECOSSISTEMA DE GERAÇÃO DE RELATÓRIOS PARA PESSOA FÍSICA**

#### **Descrição**
O objetivo deste épico é desenvolver um ecossistema distribuído para geração de relatórios financeiros sobre pessoas físicas. O sistema deverá ser capaz de processar solicitações de relatórios básicos e completos, garantindo a correta cobrança dos valores, a integridade dos dados e a alta disponibilidade dos serviços.

O sistema será composto por **quatro aplicações independentes** que se comunicam entre si via REST e mensageria (RabbitMQ). Além disso, deverá garantir resiliência em caso de falhas e ser implementado seguindo boas práticas de microsserviços, incluindo escalabilidade, testes unitários, e controle de transações com rollback.

---

### **Justificativa**
Este épico foi definido para atender às necessidades do cliente, fornecendo um sistema modular e distribuído para geração de relatórios com alto desempenho, segurança e resiliência. A arquitetura baseada em microsserviços permite escalabilidade e facilita a manutenção a longo prazo.

Dada a necessidade de cobrança pelos relatórios, é essencial que o sistema mantenha um controle financeiro preciso e implemente rollback adequado em caso de falhas. Além disso, como o relatório completo depende da execução simultânea de dois serviços, a solução deve garantir a execução assíncrona e a consolidação dos resultados.

---

### **Requisitos Funcionais**

1️ **Solicitação de Relatório:** O sistema deve permitir que o usuário solicite a geração de um relatório informando **CPF** e **tipo de relatório** (**básico** ou **completo**).

2️ **Relatório Básico:** O relatório básico deve conter as seguintes informações públicas:
   - Nome  
   - Sexo  
   - Nacionalidade  

3️ **Relatório Completo:** O relatório completo deve conter todas as informações do relatório básico, além de:
   - Endereço  
   - Telefone  
   - Documentos (RG e CPF)  

4️ **Restrição de Relatório por CPF:** O sistema deve validar o CPF e, **caso a soma dos números seja igual a 44**, o relatório **não poderá ser exibido** e deve retornar uma mensagem informando que o acesso é negado.

5️ **Cobrança do Relatório:** O sistema deve cobrar **R$5,00** pelo relatório básico e **R$10,00** pelo relatório completo. 

6️ **Registro da Cobrança no Banco de Dados:** O valor da cobrança deve ser **salvo no banco de dados antes da geração do relatório** e **incluído na resposta da API**.

7 **Geração Assíncrona do Relatório Completo:** O relatório completo deve ser gerado **de forma assíncrona**, acionando os serviços responsáveis por **Relatório Básico e Relatório Completo** para consolidar os dados.

8 **Consolidação do Relatório Completo:** O sistema deve consolidar as informações recebidas dos microsserviços e **retornar um JSON único** ao usuário.

9 **Mecanismo de Reexecução em Caso de Falha:** Caso um dos microsserviços de relatório falhe, o sistema deve realizar **duas novas tentativas**, com um intervalo de **300ms** entre elas.

10 **Rollback da Cobrança em Caso de Falha:** Caso a geração do relatório completo falhe **mesmo após as tentativas**, o sistema deve:
   - **Realizar rollback da cobrança** do relatório completo no serviço financeiro.  
   - **Cobrar apenas o valor do relatório básico** e retornar os dados disponíveis.

11 **Cobrança Assíncrona:** O serviço financeiro deve processar as cobranças **de forma assíncrona**, utilizando **RabbitMQ e Spring Cloud Stream**.

12 **Padrão de Comunicação REST e JSON:** O sistema deve **expor endpoints REST** e **responder todas as requisições no formato JSON**.

13 **Documentação Opcional via Swagger:** A aplicação **pode ser documentada via Swagger** para facilitar a integração com outras aplicações e consumidores da API.

---

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
#### **Fluxo de Comunicação entre os Serviços**
1. **Usuário → APP1 (Entrada - API Gateway e Orquestrador)**
   - O usuário faz uma requisição REST (`POST /api/v1/reports`) informando CPF e tipo de relatório (básico ou completo).
   - O APP1 valida o CPF. Se a soma dos dígitos for **44**, retorna um erro e finaliza a requisição.
   - O APP1 registra a cobrança no banco de dados e envia uma mensagem ao **RabbitMQ** para processar a cobrança no APP4 (Financeiro).

2. **APP1 → APP2 e APP3 (Serviços de Relatórios)**
   - Se o usuário solicitou um **relatório básico**, o **APP1** faz uma **requisição REST síncrona** para o **APP2** (`GET /api/v1/basic-report/{cpf}`).
   - Se o usuário solicitou um **relatório completo**, o **APP1** faz **requisições REST assíncronas** para o **APP2 e APP3** (`GET /api/v1/full-report/{cpf}`).
   - Se o **APP3 falhar**, o **APP1** reenvia a requisição **duas vezes com um intervalo de 300ms**.

3. **APP3 (Relatório Completo) → APP1**
   - O **APP3** processa a requisição de forma **assíncrona**.
   - Após obter os dados detalhados (Endereço, Telefone, Documentos), retorna uma resposta **JSON via REST** para o **APP1**.
   - O **APP1** consolida os dados do **APP2 e APP3** e retorna um JSON único ao usuário.

4. **APP1 (Entrada) → APP4 (Financeiro) via RabbitMQ**
   - O **APP1** envia uma mensagem para o **RabbitMQ** com os detalhes da cobrança.
   - O **APP4 (Financeiro)** escuta essa mensagem e processa a cobrança.
   - Se o **APP3 falhar**, o **APP1** publica um **evento de rollback no RabbitMQ**, e o **APP4** ajusta a cobrança para o relatório básico.
---
### **Histórias de Usuário**
Histórias de usuário organizadas por cada um dos quatro aplicativos.

### **Aplicação 1: Entrada (API Gateway e Orquestrador)**

#### **1 Solicitação do Relatório**
**Como usuário, quero solicitar a geração de um relatório informando meu CPF e o tipo de relatório (básico ou completo), para obter informações sobre uma pessoa física.**  
🛠 **Critérios de Aceitação:**  
✅ O usuário deve poder escolher entre **relatório básico** e **relatório completo**.  
✅ A solicitação deve ser feita via **endpoint REST**.  
✅ O sistema deve retornar o JSON com os dados do relatório gerado.  

#### **2 Validação de CPF com Restrição**
**Como sistema, quero validar se o CPF informado possui soma dos dígitos igual a 44, para restringir a exibição do relatório e informar ao usuário que o acesso é negado.**  
🛠 **Critérios de Aceitação:**  
✅ O sistema deve somar os dígitos do CPF recebido.  
✅ Caso a soma seja **igual a 44**, a requisição deve ser **negada** com uma mensagem informativa.  
✅ O sistema **não deve prosseguir** com a geração do relatório nem cobrar o usuário nesse caso.  

#### **3 Registro da Cobrança Antes da Geração do Relatório**
**Como sistema, quero registrar no banco de dados o valor da cobrança antes de iniciar a geração do relatório, garantindo rastreabilidade do pagamento.**  
🛠 **Critérios de Aceitação:**  
✅ O sistema deve gravar no banco **o valor a ser cobrado** assim que a solicitação for recebida.  
✅ O valor cobrado deve ser de **R$5,00 para o relatório básico** e **R$10,00 para o relatório completo**.  
✅ Se o relatório não puder ser gerado, a cobrança deve ser **desfeita (rollback financeiro)**.  

#### **4 Retorno do Valor Cobrado na Resposta da API**
**Como sistema, quero incluir o valor cobrado na resposta da API, para que o usuário tenha visibilidade do custo da solicitação.**  
🛠 **Critérios de Aceitação:**  
✅ A resposta JSON deve conter o campo `"valorCobranca"` informando **R$5,00** ou **R$10,00** conforme o tipo do relatório solicitado.  
✅ Caso ocorra um rollback financeiro, a resposta deve indicar que a cobrança foi **revertida** e informar o novo valor (R$5,00).  

#### **5 Execução Assíncrona do Relatório Completo**
**Como sistema, quero garantir que a requisição de um relatório completo execute os microsserviços responsáveis de forma assíncrona, otimizando o tempo de resposta.**  
🛠 **Critérios de Aceitação:**  
✅ O sistema deve **chamar os serviços de Relatório Básico e Relatório Completo** em paralelo.  
✅ A requisição deve ser **não bloqueante**, permitindo a execução em segundo plano.  
✅ O sistema deve aguardar as respostas antes de consolidar os dados e enviar ao usuário.  

#### **6 Consolidação dos Dados dos Relatórios**
**Como sistema, quero consolidar os dados retornados pelos microsserviços de relatório e devolver um JSON único ao usuário, garantindo que o relatório completo contenha todas as informações corretamente organizadas.**  
🛠 **Critérios de Aceitação:**  
✅ O JSON final deve conter **os dados do relatório básico** quando solicitado.  
✅ O JSON final deve conter **os dados do relatório completo** caso tenha sido gerado com sucesso.  
✅ Caso o relatório completo falhe, a resposta deve conter **apenas as informações do relatório básico** e a cobrança ajustada.  

#### **7 Reexecução em Caso de Falha**
**Como sistema, quero realizar até duas novas tentativas automáticas, com um intervalo de 300ms entre elas, caso um dos serviços de relatório falhe, para aumentar a resiliência da aplicação.**  
🛠 **Critérios de Aceitação:**  
✅ Se um dos serviços falhar, o sistema deve aguardar **300ms** antes de tentar novamente.  
✅ O sistema deve realizar **até 2 tentativas adicionais** antes de considerar a requisição como falha.  
✅ Caso todas as tentativas falhem, a requisição deve ser **ajustada para um relatório básico** com cobrança reduzida.  

#### **8 Rollback da Cobrança e Fallback para Relatório Básico**
**Como sistema, quero garantir rollback da cobrança do relatório completo caso a geração falhe mesmo após as tentativas, e cobrar apenas o relatório básico, para que o usuário pague apenas pelo serviço efetivamente entregue.**  
🛠 **Critérios de Aceitação:**  
✅ Se o relatório completo não puder ser gerado, o sistema deve **reverter a cobrança** no serviço financeiro.  
✅ O sistema deve **cobrar apenas R$5,00** pelo relatório básico e incluir essa informação na resposta.  
✅ O JSON retornado ao usuário deve conter **somente os dados do relatório básico**.  

#### **9 Comunicação REST e JSON**
**Como sistema, quero expor endpoints REST e garantir que todas as respostas sejam retornadas no formato JSON, garantindo compatibilidade com outras aplicações.**  
🛠 **Critérios de Aceitação:**  
✅ Todos os endpoints devem seguir o padrão REST (`/api/v1/reports`).  
✅ O formato de resposta deve ser **estruturado em JSON**.  
✅ O sistema deve seguir boas práticas de API, como uso de **status codes adequados** (`200 OK`, `400 Bad Request`, `500 Internal Server Error`).  

#### **10 Documentação via Swagger (Opcional)**
**Como sistema, posso disponibilizar documentação da API via Swagger, para facilitar a integração e uso dos endpoints.**  
🛠 **Critérios de Aceitação:**  
✅ Se implementado, a documentação Swagger deve listar **todos os endpoints** disponíveis.  
✅ O Swagger deve permitir **testar os endpoints diretamente** via interface web.  
✅ A documentação deve conter **exemplos de requisição e resposta** para cada endpoint.  

---
### **Aplicação 2: Relatório Básico**

#### **1️ Consulta de Informações Públicas de um CPF**  
**Como sistema, quero fornecer informações públicas de um CPF informado, incluindo Nome, Sexo e Nacionalidade, para atender às solicitações de relatórios básicos.**  
🛠 **Critérios de Aceitação:**  
✅ O sistema deve receber um **CPF válido** e retornar as informações básicas.  
✅ O retorno deve conter os seguintes dados:
   - `"nome"`: Nome completo da pessoa  
   - `"sexo"`: Masculino/Feminino  
   - `"nacionalidade"`: Nacionalidade do indivíduo  
✅ Se o CPF for inválido ou não existir na base, o sistema deve retornar um erro **400 - Bad Request**.  

#### **2️ Exposição de API REST para Consumo Externo**  
**Como sistema, quero expor um endpoint REST que permita o consumo da API pelo serviço de entrada, garantindo que os relatórios básicos possam ser gerados corretamente.**  
🛠 **Critérios de Aceitação:**  
✅ O endpoint REST deve seguir o padrão:
   - `GET /api/v1/basic-report/{cpf}`  
✅ A resposta deve ser formatada em **JSON**.  
✅ O endpoint deve validar **o formato do CPF** antes de consultar os dados.  
✅ O tempo de resposta da API deve ser otimizado para **baixo tempo de latência**.  

#### **3️ Tratamento de Erros e Restrições**  
**Como sistema, quero garantir que CPFs inválidos ou não encontrados sejam tratados corretamente, evitando inconsistências nos relatórios.**  
🛠 **Critérios de Aceitação:**  
✅ Se o CPF for inválido ou mal formatado, a API deve retornar **400 - Bad Request** com uma mensagem descritiva.  
✅ Se o CPF não for encontrado na base, a API deve retornar **404 - Not Found**.  
✅ Em caso de falha inesperada, a API deve retornar **500 - Internal Server Error**, registrando logs detalhados.  

#### **4 Restrições de Acesso aos Dados Públicos**  
**Como sistema, quero garantir que apenas CPFs autorizados possam ser consultados, para proteger dados sensíveis e evitar abusos.**  
🛠 **Critérios de Aceitação:**  
✅ O acesso à API deve ser restrito via **token JWT** (se necessário).  
✅ A API deve permitir apenas consultas provenientes do serviço de entrada autorizado.  
✅ Consultas excessivas ou fora dos padrões devem ser registradas para auditoria.  

---

### **Aplicação 3: Relatório Completo**

#### **1️ Consulta de Informações Detalhadas de um CPF**  
**Como sistema, quero fornecer informações detalhadas de um CPF informado, incluindo Endereço, Telefone e Documentos (RG e CPF), para complementar o Relatório Básico.**  
🛠 **Critérios de Aceitação:**  
✅ O sistema deve receber um **CPF válido** e retornar as seguintes informações adicionais:  
   - `"endereco"`: Rua, número, bairro, cidade, estado e CEP  
   - `"telefone"`: Número de telefone associado ao CPF  
   - `"documentos"`: RG e CPF  
✅ O formato de resposta deve ser **JSON** e seguir um padrão estruturado.  
✅ Se o CPF for inválido ou não existir na base, o sistema deve retornar um erro **400 - Bad Request**.  

#### **2️ Execução Assíncrona e Paralela**  
**Como sistema, quero garantir que minha execução seja feita de forma assíncrona, permitindo que o serviço de entrada processe a requisição sem bloqueios.**  
🛠 **Critérios de Aceitação:**  
✅ A aplicação **não deve bloquear a requisição** enquanto processa os dados.  
✅ A chamada ao serviço deve permitir **execução em segundo plano**, sem impactar a API de entrada.  
✅ O serviço deve garantir **alta disponibilidade**, respondendo de forma eficiente.  

#### **3️ Reexecução Automática em Caso de Falha**  
**Como sistema, quero garantir que, em caso de falha, minha execução seja reprocessada automaticamente até duas vezes, com um intervalo de 300ms entre as tentativas.**  
🛠 **Critérios de Aceitação:**  
✅ Se a consulta falhar, o sistema deve aguardar **300ms** antes de tentar novamente.  
✅ O sistema deve realizar até **2 tentativas adicionais** antes de considerar a requisição como falha.  
✅ Se todas as tentativas falharem, o sistema deve **retornar um erro ao serviço de entrada**, que deverá ajustar a resposta para um **relatório básico** e cobrar apenas R$5,00.  

#### **4️ Exposição de API REST para Consumo Externo**  
**Como sistema, quero expor um endpoint REST que permita o consumo da API pelo serviço de entrada, garantindo que os relatórios completos possam ser gerados corretamente.**  
🛠 **Critérios de Aceitação:**  
✅ O endpoint REST deve seguir o padrão:
   - `GET /api/v1/full-report/{cpf}`  
✅ A resposta deve ser formatada em **JSON**.  
✅ O endpoint deve validar **o formato do CPF** antes de consultar os dados.  
✅ O tempo de resposta da API deve ser otimizado para **baixo tempo de latência**.  

#### **5️ Tratamento de Erros e Restrições**  
**Como sistema, quero garantir que CPFs inválidos ou não encontrados sejam tratados corretamente, evitando inconsistências nos relatórios.**  
🛠 **Critérios de Aceitação:**  
✅ Se o CPF for inválido ou mal formatado, a API deve retornar **400 - Bad Request** com uma mensagem descritiva.  
✅ Se o CPF não for encontrado na base, a API deve retornar **404 - Not Found**.  
✅ Em caso de falha inesperada, a API deve retornar **500 - Internal Server Error**, registrando logs detalhados.  

#### **6️ Restrições de Acesso aos Dados Detalhados**  
**Como sistema, quero garantir que apenas CPFs autorizados possam ser consultados, para proteger dados sensíveis e evitar abusos.**  
🛠 **Critérios de Aceitação:**  
✅ O acesso à API deve ser restrito via **token JWT** (se necessário).  
✅ A API deve permitir apenas consultas provenientes do serviço de entrada autorizado.  
✅ Consultas excessivas ou fora dos padrões devem ser registradas para auditoria.  

---

### **Aplicação 4: Financeiro**

#### **1️ Processamento de Cobranças via Mensageria**  
**Como sistema, quero processar as cobranças dos relatórios utilizando RabbitMQ, garantindo que a comunicação entre serviços seja assíncrona e eficiente.**  
🛠 **Critérios de Aceitação:**  
✅ O sistema deve consumir mensagens de cobrança enviadas pelo **serviço de entrada** via **RabbitMQ**.  
✅ As mensagens devem conter os dados necessários para a cobrança:  
   - `"cpf"`: Identificador do usuário  
   - `"valor"`: Valor a ser cobrado (R$5,00 ou R$10,00)  
   - `"tipoRelatorio"`: Básico ou Completo  
✅ O sistema deve **confirmar o processamento** da mensagem para evitar duplicação.  

#### **2️ Escuta de Mensagens de Cobrança**  
**Como sistema, quero escutar mensagens de cobrança enviadas pelo serviço de entrada, para garantir que todas as requisições sejam corretamente processadas.**  
🛠 **Critérios de Aceitação:**  
✅ O sistema deve consumir mensagens do tópico de cobrança no **RabbitMQ**.  
✅ Caso a mensagem esteja corrompida ou inválida, deve ser rejeitada e registrada em logs para auditoria.  
✅ O sistema deve suportar **reconexão automática** ao RabbitMQ em caso de falha na conexão.  

#### **3️ Registro de Cobranças Bem-Sucedidas e Falhas para Auditoria**  
**Como sistema, quero registrar cobranças bem-sucedidas e falhas em um banco de dados, garantindo rastreabilidade e auditoria.**  
🛠 **Critérios de Aceitação:**  
✅ As cobranças bem-sucedidas devem ser persistidas no banco de dados com os seguintes dados:  
   - `"cpf"`: Identificador do usuário  
   - `"valor"`: Valor cobrado  
   - `"tipoRelatorio"`: Básico ou Completo  
   - `"status"`: `"SUCESSO"`  
   - `"dataHora"`: Data e hora do processamento  
✅ Em caso de falha na cobrança, o sistema deve registrar no banco de dados:  
   - `"status"`: `"FALHA"`  
   - `"motivo"`: Descrição do erro ocorrido  
✅ O sistema deve expor um **endpoint de consulta de auditoria**, permitindo recuperar cobranças processadas e suas falhas.  

#### **4️ Rollback da Cobrança em Caso de Falha**  
**Como sistema, quero garantir que o rollback da cobrança seja processado corretamente em caso de falha na geração do relatório completo, para evitar cobranças indevidas.**  
🛠 **Critérios de Aceitação:**  
✅ Se o serviço de **Relatório Completo** falhar após todas as tentativas, o sistema deve processar o rollback da cobrança e **ajustar o valor para R$5,00** (relatório básico).  
✅ O rollback deve ser realizado enviando uma **nova mensagem para o RabbitMQ**, garantindo a reversão da cobrança.  
✅ A reversão da cobrança deve ser registrada na auditoria, marcando a transação original como `"REVERTIDO"`.  
✅ O sistema deve garantir que **nenhum usuário seja cobrado erroneamente** em caso de falha do Relatório Completo.  

---

### **Backlog das Tarefas SCRUM**
Aqui estão todas as **tarefas** organizadas em **sprints**, priorizando as mais críticas primeiro.

### **📌 Sprint 1: Configuração Inicial e Estruturação**

📌 **Objetivo da Sprint 1:**  
Esta sprint tem como foco **configurar a base do projeto**, garantindo que todas as aplicações estejam **estruturadas corretamente**, com suas dependências configuradas e **prontas para desenvolvimento**. Além disso, as **principais integrações entre os serviços** (REST e RabbitMQ) serão implementadas e testadas.

### **📌 Tarefas Detalhadas**
#### **1️ Criar estrutura do projeto e repositórios**  
📌 **Descrição:**  
Configurar o repositório Git e criar a estrutura inicial do projeto para todas as aplicações:  
   - **Entrada (API Gateway e Orquestrador)**  
   - **Relatório Básico**  
   - **Relatório Completo**  
   - **Financeiro**  

🛠 **Critérios de Aceitação:**  
✅ Criar repositório Git e definir a estrutura do código-fonte.  
✅ Criar um projeto **Spring Boot 3.4.2 com Java 17** para cada aplicação.  
✅ Configurar o **.gitignore** para evitar arquivos desnecessários no repositório.  
✅ Criar **pacotes organizados** (`controller`, `service`, `repository`, `model`, etc.).  
✅ Configurar um README inicial com a descrição do projeto e estrutura do repositório.  

#### **2️ Configurar aplicações Spring Boot 3.4.2 com Java 17**  
📌 **Descrição:**  
Configurar **Spring Boot 3.4.2** e definir dependências principais no `pom.xml` para cada aplicação.  

🛠 **Critérios de Aceitação:**  
✅ Criar e configurar os projetos Spring Boot com o **Spring Initializr**.  
✅ Definir dependências necessárias para cada aplicação:  
   - Entrada: **Spring Web, Spring Boot Actuator, Feign Client, OpenAPI (Swagger - opcional)**.  
   - Relatórios: **Spring Web, Spring Boot Actuator, JPA, H2 ou PostgreSQL**.  
   - Financeiro: **Spring Boot Actuator, Spring Cloud Stream (RabbitMQ)**.  
✅ Criar `application.yml` com as configurações iniciais de cada aplicação.  
✅ Configurar **porta dos serviços** para evitar conflitos.  

#### **3️ Criar estrutura de banco de dados relacional no serviço de entrada**  
📌 **Descrição:**  
O serviço **Entrada** deve ter um **banco de dados relacional** para armazenar as cobranças antes da geração dos relatórios.  

🛠 **Critérios de Aceitação:**  
✅ Criar **entidades JPA** para persistência dos dados no banco.  
✅ Configurar **Spring Data JPA** e definir repositórios para acesso aos dados.  
✅ Definir **scripts SQL iniciais** para criar tabelas de cobrança (`cobranca`, `auditoria`, etc.).  
✅ Configurar **H2 (para ambiente de desenvolvimento)** e **PostgreSQL (para produção)**.  

#### **4️ Implementar comunicação entre serviços via REST**  
📌 **Descrição:**  
Cada serviço deve se comunicar via **REST**, garantindo **integração eficiente** entre os microsserviços.  

🛠 **Critérios de Aceitação:**  
✅ Definir **endpoints REST** para comunicação entre os serviços.  
✅ Implementar **Feign Client** no serviço de **Entrada** para consumir os serviços de Relatórios e Financeiro.  
✅ Configurar **tratamento de erros e timeouts** para chamadas externas.  
✅ Criar **testes iniciais de integração** para validar a comunicação.  

#### **5️ Configurar mensageria RabbitMQ no serviço financeiro**  
📌 **Descrição:**  
O serviço **Financeiro** deve processar mensagens de cobrança **assíncronas**, garantindo que os pagamentos sejam controlados corretamente.  

🛠 **Critérios de Aceitação:**  
✅ Configurar **RabbitMQ** no ambiente local e definir filas de mensagens.  
✅ Implementar **Spring Cloud Stream** para consumir mensagens de cobrança.  
✅ Criar **produtores e consumidores** de mensagens no RabbitMQ.  
✅ Criar **testes unitários** para validar a comunicação com RabbitMQ.  

#### **6️ Criar testes unitários iniciais para as classes de domínio**  
📌 **Descrição:**  
Criar **testes unitários com JUnit 5 e Mockito** para garantir a integridade das classes de domínio antes de avançar no desenvolvimento.  

🛠 **Critérios de Aceitação:**  
✅ Criar testes para **entidades e repositórios** (Spring Data JPA).  
✅ Criar testes para **serviços** utilizando **Mockito**.  
✅ Garantir que os testes **rodam automaticamente** no pipeline de CI/CD.  

---

### **📌 Sprint 2: Implementação das Funcionalidades Principais**

📌 **Objetivo da Sprint 2:**  
Nesta sprint, as **funcionalidades centrais** do ecossistema serão implementadas. Isso inclui a criação dos **endpoints principais** em cada microsserviço, garantindo que os relatórios possam ser gerados corretamente e que as cobranças sejam processadas via RabbitMQ. Além disso, serão implementados **testes unitários para regras críticas**, como **validação de CPF e rollback financeiro**.

### **📌 Tarefas Detalhadas**
#### **7️ Implementar serviço de Entrada (API Gateway e Orquestrador)**  
📌 **Descrição:**  
O serviço **Entrada** será responsável por **receber as requisições dos usuários**, validar os dados e **orquestrar** chamadas para os serviços de relatório e financeiro.  

🛠 **Critérios de Aceitação:**  
✅ Criar **endpoint REST** para solicitação de relatórios:  
   - `POST /api/v1/reports` (entrada de CPF e tipo do relatório).  
✅ Implementar lógica para **validação de CPF** e **restrição caso a soma dos dígitos seja 44**.  
✅ Chamar os serviços de **Relatório Básico e Completo de forma assíncrona** quando necessário.  
✅ Persistir o **valor cobrado** antes da geração do relatório no banco de dados.  
✅ Enviar **mensagem de cobrança para o RabbitMQ** antes da geração do relatório.  
✅ Consolidar os dados retornados dos serviços de relatório e **retornar um JSON único**.  

#### **8️ Implementar serviço de Relatório Básico**  
📌 **Descrição:**  
O serviço **Relatório Básico** fornecerá **dados públicos do CPF** e será acessado pelo serviço de **Entrada**.  

🛠 **Critérios de Aceitação:**  
✅ Criar **endpoint REST** para recuperar dados básicos do CPF:  
   - `GET /api/v1/basic-report/{cpf}`  
✅ Implementar lógica para retornar **Nome, Sexo e Nacionalidade**.  
✅ Implementar **tratamento de erro** para CPF inválido ou inexistente.  
✅ Criar **testes unitários** para garantir a correta geração do relatório básico.  

#### **9️ Implementar serviço de Relatório Completo**  
📌 **Descrição:**  
O serviço **Relatório Completo** fornecerá **dados detalhados** e será acessado pelo serviço de **Entrada**.  

🛠 **Critérios de Aceitação:**  
✅ Criar **endpoint REST** para recuperar dados completos do CPF:  
   - `GET /api/v1/full-report/{cpf}`  
✅ Implementar lógica para retornar **Endereço, Telefone e Documentos (RG e CPF)**.  
✅ Implementar **execução assíncrona** para permitir que o serviço de Entrada processe a requisição sem bloqueios.  
✅ Implementar **mecanismo de retry** em caso de falha, com **2 novas tentativas e 300ms de intervalo**.  
✅ Criar **testes unitários** para validar o serviço de relatório completo.  

#### **10 Implementar serviço Financeiro com RabbitMQ**  
📌 **Descrição:**  
O serviço **Financeiro** será responsável por **processar as cobranças via RabbitMQ** e garantir rollback quando necessário.  

🛠 **Critérios de Aceitação:**  
✅ Criar **consumer de mensagens** para processar cobranças recebidas do serviço de Entrada.  
✅ Implementar **persistência das cobranças** bem-sucedidas e falhas no banco de dados.  
✅ Criar **endpoint de consulta de auditoria** para visualizar cobranças processadas.  
✅ Criar **mecanismo de rollback**, revertendo a cobrança caso o relatório completo falhe.  
✅ Criar **testes unitários** para validar a lógica de cobrança e rollback.  

#### **1️1 Criar testes unitários para validação de CPF e lógica de restrição**  
📌 **Descrição:**  
Os testes devem validar **regras críticas de CPF**, garantindo que **CPFs inválidos ou restritos** sejam corretamente identificados.  

🛠 **Critérios de Aceitação:**  
✅ Testar **validação de CPF** garantindo que formatos inválidos sejam rejeitados.  
✅ Testar **regra de soma dos dígitos do CPF = 44**, garantindo que o relatório seja bloqueado.  
✅ Testar **respostas adequadas da API** (`400 Bad Request` para CPF inválido, `403 Forbidden` para CPF restrito).  
✅ Cobertura de código superior a **90%** para essas regras.  

#### **1️2 Criar testes unitários para rollback da cobrança**  
📌 **Descrição:**  
Os testes devem garantir que, **caso o relatório completo falhe**, o rollback da cobrança seja realizado corretamente.  

🛠 **Critérios de Aceitação:**  
✅ Testar **rollback da cobrança no banco de dados** caso o relatório completo falhe.  
✅ Testar **envio de mensagem de reversão** para o RabbitMQ.  
✅ Garantir que o **valor correto (R$5,00) seja cobrado** após o fallback para relatório básico.  
✅ Cobertura de código superior a **90%** para essa funcionalidade.  

---

### **📌 Sprint 3: Resiliência, Integração e Testes Finais**

📌 **Objetivo da Sprint 3:**  
Nesta sprint, o foco será **garantir a resiliência e robustez do sistema**, implementando **mecanismos de retry e rollback**, além de **testes avançados** para validar sua estabilidade e performance. Também serão incluídos **documentação, arquitetura e suporte a Docker** para facilitar a integração e o deploy.


### **📌 Tarefas Detalhadas**

#### **1️3 Implementar mecanismo de reexecução em caso de falha nos relatórios (retry)**  
📌 **Descrição:**  
Se um dos serviços de relatório falhar, o sistema deve tentar novamente antes de considerar a requisição como erro.  

🛠 **Critérios de Aceitação:**  
✅ Implementar **retry automático** para chamadas aos serviços de relatório, com **até 2 tentativas** e **intervalo de 300ms**.  
✅ Caso o serviço responda com erro **5XX (falha interna)**, o sistema deve tentar novamente.  
✅ Se todas as tentativas falharem, o sistema deve **acionar o rollback da cobrança** e retornar o relatório básico.  
✅ Criar **testes unitários e de integração** simulando falhas e validando o retry.  

#### **1️4 Implementar rollback automático em caso de falha total na geração do relatório completo**  
📌 **Descrição:**  
Caso o relatório completo não possa ser gerado, o sistema deve garantir que **a cobrança seja revertida** e o relatório básico seja entregue. 

🛠 **Critérios de Aceitação:**  
✅ Se todas as tentativas de retry falharem, o sistema deve:  
   - **Reverter a cobrança do relatório completo no serviço financeiro**.  
   - **Cobrar apenas R$5,00 pelo relatório básico**.  
   - **Retornar um JSON contendo somente os dados do relatório básico**.  
✅ O rollback da cobrança deve ser **registrado no banco de dados e auditado**.  
✅ Criar **testes unitários e de integração** para garantir o correto funcionamento do rollback.  

#### **1️5 Criar documentação Swagger para APIs**  
📌 **Descrição:**  
A documentação da API deve permitir que desenvolvedores externos entendam e consumam os endpoints do sistema.  

🛠 **Critérios de Aceitação:**  
✅ Criar documentação Swagger para todas as APIs, incluindo:  
   - **Entrada (API Gateway)**  
   - **Relatório Básico**  
   - **Relatório Completo**  
   - **Financeiro**  
✅ Cada endpoint deve ter **descrição detalhada dos parâmetros e respostas esperadas**.  
✅ Incluir **exemplos de requisição e resposta** para facilitar o uso da API.  
✅ Permitir **testes diretos via interface Swagger UI**.  

#### **1️6 Implementar Docker e Docker Compose (opcional)**  
📌 **Descrição:**  
A aplicação deve ter suporte para execução via **Docker**, facilitando deploy e testes locais.  

🛠 **Critérios de Aceitação:**  
✅ Criar **Dockerfile** para cada aplicação, garantindo que todas as dependências sejam instaladas corretamente.  
✅ Criar **Docker Compose** para facilitar a execução do ecossistema completo com um único comando.  
✅ Configurar **volumes e networks** no Docker Compose para garantir a comunicação entre os serviços.  
✅ Testar a execução dos containers e validar que todas as aplicações sobem corretamente.  

#### **1️7 Criar desenho da arquitetura do ecossistema**  
📌 **Descrição:**  
A arquitetura do sistema deve ser documentada visualmente para facilitar o entendimento da solução.  

🛠 **Critérios de Aceitação:**  
✅ Criar diagrama de arquitetura mostrando a comunicação entre os serviços.  
✅ Definir os **principais fluxos de dados** entre os microsserviços.  
✅ Incluir detalhes sobre **RabbitMQ, REST APIs e banco de dados**.  
✅ Criar documentação explicando os principais componentes do sistema.  

#### **1️8 Realizar testes integrados de ponta a ponta**  
📌 **Descrição:**  
Os testes de integração devem validar que todos os microsserviços **se comunicam corretamente** e que o fluxo completo de geração de relatório funciona sem erros.  

🛠 **Critérios de Aceitação:**  
✅ Criar **testes automatizados** que simulam uma requisição completa do usuário:  
   - Entrada de CPF e solicitação de relatório.  
   - Validação da cobrança e geração do relatório.  
   - Comunicação entre os serviços e retorno correto da resposta.  
✅ Simular **cenários de erro**, como:  
   - Serviço de relatório indisponível.  
   - Falha na comunicação RabbitMQ.  
   - Timeout na geração do relatório.  
✅ Garantir que os testes **passam em 100% das execuções** antes do deploy.  

#### **1️9 Validar performance e comportamento em cenários de erro**  
📌 **Descrição:**  
Antes do go-live, a aplicação deve ser testada sob **carga** para garantir que suporta múltiplas requisições simultâneas sem degradação de performance.  

🛠 **Critérios de Aceitação:**  
✅ Testar **tempo de resposta médio** para geração dos relatórios.  
✅ Testar **comportamento sob carga alta** (100+ requisições simultâneas).  
✅ Validar **uso de memória e CPU**, garantindo que os serviços se mantêm estáveis.  
✅ Simular falhas em serviços críticos e validar a resposta do sistema.  

---
