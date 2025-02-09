# ÉPICO: Geração de Relatórios para Pessoa Física

## Descrição:
Desenvolver um ecossistema composto por quatro aplicativos para permitir que um cliente solicite a geração de relatórios de pessoa física, com opção de relatório básico ou completo, realizando a cobrança e gerenciando processos assíncronos de forma resiliente. A arquitetura deve ser baseada em **Java 17+**, **Spring Boot 3.4.2**, e deve garantir robustez utilizando mensageria com **RabbitMQ** e reprocessamento automático em caso de falha.

## Objetivo:
- Criar um sistema distribuído e escalável para geração de relatórios.
- Garantir resiliência e confiabilidade na geração do relatório completo.
- Implementar fluxo de rollback caso a geração do relatório completo falhe.
- Garantir segurança e rastreabilidade das requisições.
- Implementar testes automatizados para validar o comportamento do sistema.

## Aplicativos:
1. **Entrada**: Responsável por receber requisições, validar dados, persistir a cobrança e consolidar o resultado.
2. **Relatório Básico**: Responsável por gerar informações básicas da pessoa física.
3. **Relatório Completo**: Responsável por gerar informações adicionais (endereço, telefone, documentos).
4. **Financeiro**: Responsável por processar a cobrança, executar rollback em caso de erro e realizar integração assíncrona via RabbitMQ.

---

# HISTÓRIAS DE USUÁRIO

## US001 - Solicitar Relatório
**Como** um cliente da aplicação, **eu quero** solicitar um relatório básico ou completo **para que** eu obtenha informações sobre uma pessoa física.

### Tarefas:
- Criar o endpoint de solicitação de relatório.
- Implementar a persistência da cobrança antes da geração do relatório.
- Implementar validação do CPF para verificação de restrição.
- Criar a lógica para definir se o relatório solicitado é básico ou completo.

### Subtarefas:
- Criar classe de request para o endpoint.
- Criar service para processar a solicitação.
- Implementar persistência com JPA.
- Implementar retorno JSON padronizado.
- Implementar logging e auditoria da requisição.

---

## US002 - Geração do Relatório Completo

### Tarefas:
- Implementar chamadas assíncronas para os microsserviços de relatório.
- Consolidar os resultados do relatório básico e completo.
- Implementar tentativa de reprocessamento em caso de falha.
- Implementar rollback da cobrança caso a geração falhe.

### Subtarefas:
- Criar feign client para chamadas assíncronas.
- Criar método de tentativa com intervalo de 300ms.
- Criar mecanismo de fallback para uso do relatório básico.
- Implementar testes unitários e de integração.

---

## US003 - Geração de Relatórios via Mensageria

### Tarefas:
- Implementar mensageria com RabbitMQ para processar cobrança.
- Implementar reprocessamento em caso de falha no financeiro.
- Criar logs e auditoria para rastreabilidade.

### Subtarefas:
- Configurar RabbitMQ no projeto.
- Criar classes de mensagem e consumidor RabbitMQ.
- Implementar testes para garantir a confiabilidade da comunicação.
- Criar dashboard para monitoramento de eventos RabbitMQ.

---

# BDD (Behavior-Driven Development)

## Cenário 1: Solicitação de Relatório Básico
**Dado** que um cliente solicita um relatório básico
**Quando** a requisição é recebida pelo sistema
**Então** o sistema deve cobrar R$5,00
**E** deve retornar um JSON contendo Nome, Sexo e Nacionalidade

## Cenário 2: Solicitação de Relatório Completo
**Dado** que um cliente solicita um relatório completo
**Quando** a requisição é processada
**Então** o sistema deve chamar os microsserviços de Relatório Básico e Completo de forma assíncrona
**E** deve consolidar os dados retornados
**E** deve cobrar R$10,00 do cliente

## Cenário 3: Restrinção de CPF
**Dado** que um cliente solicita um relatório com CPF cujo somatório seja 44
**Quando** o sistema processa a solicitação
**Então** deve retornar erro "Relatório Restrito" sem cobrar o cliente

## Cenário 4: Falha no Relatório Completo
**Dado** que um cliente solicita um relatório completo
**Quando** o microsserviço de Relatório Completo falha
**Então** o sistema deve tentar novamente por até duas vezes com intervalo de 300ms
**E** se continuar falhando, deve reverter a cobrança e cobrar apenas pelo relatório básico

---

# Definition of Done (DoD)
- Todas as histórias de usuário implementadas e testadas.
- Testes unitários cobrindo pelo menos 90% do código.
- Pipeline CI/CD configurado e validado.
- Documentação da API gerada via Swagger.
- Logs estruturados implementados.
- Docker e Docker Compose configurados (Opcional).
- Todos os códigos revisados e aprovados no code review.

---

# Definition of Done (DoD)
- Todas as histórias de usuário implementadas e testadas.
- Testes unitários cobrindo pelo menos 90% do código.
- Pipeline CI/CD configurado e validado.
- Documentação da API gerada via Swagger.
- Logs estruturados implementados.
- Docker e Docker Compose configurados (Opcional).
- Todos os códigos revisados e aprovados no code review.

---