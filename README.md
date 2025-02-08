# Geração de Relatórios para Pessoa Física

## Descrição do Épico
O objetivo deste épico é desenvolver um ecossistema de quatro aplicações independentes que permitirão a geração de relatórios básicos e completos para pessoa física. O sistema deverá identificar o tipo de relatório solicitado pelo cliente e cobrar um valor correspondente. Adicionalmente, deve-se garantir que, se a soma dos números do CPF informado for igual a 44, o relatório não seja gerado e o cliente seja informado da restrição.

O sistema será composto por quatro aplicativos:
1. **App Entrada**: Responsável por receber as requisições dos clientes, validar os dados, calcular os valores e acionar os demais apps para geração dos relatórios.
2. **App Relatório Básico**: Responsável por gerar as informações públicas do relatório.
3. **App Relatório Completo**: Responsável por gerar as informações completas do relatório (inclusive endereço, telefone e documentos).
4. **App Financeiro**: Responsável por registrar a cobrança dos relatórios e garantir rollback caso ocorra falhas na geração do relatório completo.

## DoR (Definition of Ready)
- As regras de negócio e os critérios de aceitação foram definidos e documentados.
- O backlog das histórias foi revisado e priorizado pelo Product Owner.
- O time tem acesso ao ambiente de desenvolvimento e ferramentas necessárias.
- O time possui entendimento sobre a estrutura do ecossistema e a arquitetura definida.
- Todas as dependências externas e internas foram mapeadas.

## DoD (Definition of Done)
- O código está versionado no repositório.
- Todos os testes unitários e de integração estão implementados com cobertura superior a 80%.
- A documentação (Swagger) está atualizada e disponível.
- O rollback está funcionando corretamente em caso de falha.
- O sistema está preparado para execução em contêiner Docker.
- As mensagens são processadas corretamente pelo RabbitMQ.
- O monitoramento e logging estão configurados.
- O sistema foi validado em ambiente de teste.

---

## BDDs

### Cenário 1: Solicitação de Relatório Básico
**Dado** que um cliente solicita um relatório básico informando um CPF válido
**Quando** o App Entrada recebe a requisição
**Então** o sistema grava a cobrança de R$5,00 no banco de dados
**E** chama o App Relatório Básico
**E** retorna ao cliente as informações de Nome, Sexo e Nacionalidade

### Cenário 2: Solicitação de Relatório Completo
**Dado** que um cliente solicita um relatório completo informando um CPF válido
**Quando** o App Entrada recebe a requisição
**Então** o sistema grava a cobrança de R$10,00 no banco de dados
**E** chama os Apps Relatório Básico e Relatório Completo de forma assíncrona
**E** consolida os dados dos relatórios em um único JSON
**E** retorna ao cliente todas as informações

### Cenário 3: Validação de CPF com soma 44
**Dado** que um cliente solicita um relatório informando um CPF cuja soma dos dígitos é 44
**Quando** o App Entrada recebe a requisição
**Então** o sistema informa ao cliente que o relatório é restrito
**E** nenhuma cobrança é realizada

### Cenário 4: Falha ao Gerar Relatório Completo
**Dado** que um cliente solicita um relatório completo
**E** o App Relatório Completo está fora do ar
**Quando** o App Entrada tenta obter os dados do relatório
**Então** o sistema faz duas novas tentativas com intervalo de 300ms
**E** se o erro persistir, faz rollback da cobrança
**E** cobra apenas o valor do relatório básico
**E** retorna ao cliente apenas as informações públicas

### Cenário 5: Envio de Mensagem para o App Financeiro
**Dado** que um relatório é gerado com sucesso
**Quando** o App Entrada processa a requisição
**Então** o sistema envia uma mensagem via RabbitMQ para o App Financeiro
**E** o App Financeiro processa a cobrança

---

## Atividades Scrum

### Sprint 1: Setup do Ecossistema
- Criar repositório no Git
- Configurar ambiente local com Docker
- Definir estrutura dos projetos
- Criar documentação inicial da arquitetura

### Sprint 2: Implementação do App Entrada
- Criar endpoints para solicitação de relatório
- Implementar validação de CPF restrito
- Implementar comunicação com o banco de dados
- Implementar chamadas para os apps de relatório

### Sprint 3: Implementação dos Apps Relatório Básico e Completo
- Criar endpoints REST para retorno dos dados
- Implementar processamento assíncrono
- Garantir formatação correta da resposta

### Sprint 4: Implementação do App Financeiro
- Criar mensageria RabbitMQ
- Implementar rollback em caso de erro
- Implementar persistência da cobrança

### Sprint 5: Testes e Monitoramento
- Implementar testes unitários e de integração
- Configurar Swagger
- Configurar logging e monitoramento
- Realizar testes de carga

**Conclusão:**
Com esse planejamento detalhado, todas as funcionalidades estão bem definidas e estruturadas, garantindo uma implementação eficiente e organizada do sistema.

