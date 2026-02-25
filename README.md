# SpringSecurity

> Projeto em desenvolvimento — novas funcionalidades ainda estão sendo implementadas.

## Sobre o projeto

Este projeto é uma aplicação back-end baseada em **Spring Boot** com **Spring Security**, focada em autenticação, autorização e integração com serviços externos.  
Ele servirá como base para:

- Gerenciamento de usuários e permissões
- Envio de mensagens via API da Meta (WhatsApp)
- Envio de e-mails transacionais
- Processamento de pagamentos através de uma API de pagamento

## Dependências utilizadas

Algumas das principais dependências (podem variar conforme o `pom.xml`):

- `spring-boot-starter-web` — criação de APIs REST
- `spring-boot-starter-security` — autenticação e autorização
- `spring-boot-starter-data-jpa` — persistência de dados com JPA/Hibernate
- `spring-boot-starter-validation` — validação de dados de entrada
- `spring-boot-starter-mail` — envio de e-mails (para notificações)
- `lombok` — redução de boilerplate
- `postgresql` ou `h2` — banco de dados relacional
- Lib de **JWT** — geração e validação de tokens
- Ferramenta de documentação (ex.: `springdoc-openapi`/Swagger) — documentação da API

> As dependências podem ser ajustadas conforme o projeto evoluir.

## Integrações planejadas

### API da Meta (WhatsApp)

Será integrada uma **API da Meta (WhatsApp Business)** para envio de mensagens aos usuários, como:

- Confirmações de ações
- Alertas e notificações automáticas
- Mensagens transacionais em geral

### Envio de e-mails

O sistema fará envio de e-mails para:

- Confirmação de cadastro
- Recuperação de senha
- Notificações e avisos importantes

A integração poderá ser feita via SMTP ou provedores como SendGrid, Amazon SES, etc.

### API de pagamento

Será adicionada uma integração com uma API de pagamento (ex.: Mercado Pago, Stripe, PayPal) para:

- Processar pagamentos
- Registrar transações
- Gerenciar status de cobrança

## Estrutura do projeto

A estrutura abaixo é um modelo esperado para o projeto:

```text
SpringSecurity/
├── springsecurity/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/springsecurity/
│   │   │   │       ├── config/         # Configurações (segurança, CORS, beans, etc.)
│   │   │   │       ├── controller/     # Controllers / endpoints REST
│   │   │   │       ├── dto/            # DTOs usados nas requisições/respostas
│   │   │   │       ├── exception/      # Tratamento de erros e exceções globais
│   │   │   │       ├── model/          # Entidades de domínio / JPA
│   │   │   │       ├── repository/     # Repositórios JPA
│   │   │   │       ├── security/       # Configuração do Spring Security, filtros, JWT, etc.
│   │   │   │       ├── service/        # Regras de negócio
│   │   │   │       └── SpringSecurityApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties  # Configurações da aplicação
│   │   │       └── application-*.yml       # Perfis (dev, prod, etc.) se usados
│   │   └── test/                       # Testes unitários e de integração
│   └── pom.xml
└── README.md
```

## Status do projeto

- O projeto **ainda está em desenvolvimento**.
- Algumas funcionalidades podem não estar totalmente implementadas ou podem sofrer mudanças.
- As integrações com **API da Meta**, **e-mails** e **API de pagamento** serão adicionadas e refinadas ao longo do desenvolvimento.
