# Fourteca - Sistema de Gerenciamento de Biblioteca

[![Java CI with Maven](https://github.com/felipeVeiga03/fourteca/actions/workflows/ci.yml/badge.svg)](https://github.com/felipeVeiga03/fourteca/actions/workflows/ci.yml)

API RESTful para gerenciamento de uma biblioteca, permitindo o cadastro de livros, leitores, empréstimos e reservas.

## Pré-requisitos

Para compilar e executar este projeto, você precisará das seguintes ferramentas:

- **Java 17** ou superior
- **Maven 3.8** ou superior

## Como Executar

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/felipeVeiga03/fourteca.git
   cd fourteca
   ```

2. **Compile e execute a aplicação com o Maven:**
   ```bash
   mvn clean spring-boot:run
   ```
   Por padrão, a aplicação iniciará com o perfil `dev`.

A aplicação estará disponível em `http://localhost:8080`.

## Acesso ao Banco de Dados (H2 Console)

Com a aplicação em execução (usando o perfil `dev`), você pode inspecionar o banco de dados visualmente através do console do H2.

1. **Acesse a URL:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
2. **Preencha os campos da seguinte forma:**
   - **JDBC URL:** `jdbc:h2:file:./data/fourteca`
   - **User Name:** `sa`
   - **Password:** (deixe em branco)
3. **Clique em "Connect"**.

## Documentação da API (Swagger)

Após iniciar a aplicação, a documentação interativa da API estará disponível no Swagger UI. Acesse a seguinte URL no seu navegador:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Perfis de Configuração

O projeto utiliza perfis do Spring Boot para separar as configurações de ambiente:

- **`application-dev.yaml` (Padrão):** Utiliza o banco de dados H2 persistido em arquivo e habilita o Flyway para migrações. Ideal para desenvolvimento local.
- **`application-test.yaml`:** Utiliza um banco de dados H2 em memória e desabilita o Flyway. Usado automaticamente ao rodar os testes (`mvn test`) para garantir um ambiente limpo e isolado.

As configurações de negócio (prazos, multas, etc.) estão no arquivo principal `application.yaml`.

## Exemplos de Comandos cURL

Aqui estão alguns exemplos de comandos `curl` para interagir com a API.

### 1. Autenticar e Obter um Token

```bash
curl -X POST "http://localhost:8080/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "login": "admin",
  "senha": "password"
}'
```

### 2. Cadastrar um Novo Livro (com Token)

```bash
TOKEN="seu-token-jwt-aqui"
curl -X POST "http://localhost:8080/livros" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN" \
-d '{
  "titulo": "O Senhor dos Anéis",
  "autor": "J.R.R. Tolkien",
  "isbn": "978-85-9508-080-0",
  "disponivel": true
}'
```
