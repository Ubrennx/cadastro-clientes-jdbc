# Cadastro de Clientes API

API REST desenvolvida com **Java 17**, **Spring Boot**, **Spring Data JPA** e **PostgreSQL** para realizar o cadastro e a consulta de clientes.

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- IntelliJ IDEA
- Postman
- Git


---

## Arquitetura

O projeto foi desenvolvido utilizando a arquitetura em camadas:

```
Cliente (Postman)
        │
        ▼
ClienteController
        │
        ▼
ClienteService
        │
        ▼
ClienteRepository
        │
        ▼
Hibernate (JPA)
        │
        ▼
PostgreSQL
```

Cada camada possui uma responsabilidade específica:

- **Controller:** recebe as requisições HTTP e retorna as respostas.
- **Service:** contém a lógica da aplicação.
- **Repository:** realiza o acesso ao banco de dados utilizando Spring Data JPA.
- **Entity:** representa as tabelas do banco de dados.

---

## Funcionalidades

- Cadastrar cliente
- Listar todos os clientes
- Atualizar cliente
- Excluir cliente

---

## Endpoints

### Cadastrar Cliente

**POST** `/clientes`

Exemplo de requisição:

```json
{
    "nome": "Luiz Henrique",
    "email": "luiz@email.com",
    "idade": 21
}
```

Resposta:

```json
{
    "id": 1,
    "nome": "Luiz Henrique",
    "email": "luiz@email.com",
    "idade": 21
}
```

---

### Listar Clientes

**GET** `/clientes`

Resposta:

```json
[
    {
        "id": 1,
        "nome": "Luiz Henrique",
        "email": "luiz@email.com",
        "idade": 21
    }
]
```
---

### Atualizar Cliente

**PUT** `/clientes/{id}`

Exemplo:

```
PUT /clientes/1
```

#### Body

```json
{
    "nome": "Luiz Henrique",
    "email": "novoemail@gmail.com",
    "idade": 22
}
```
---

### Excluir Cliente

**DELETE** `/clientes/{id}`

Exemplo:

```
DELETE /clientes/1
```

---

## Configuração do Banco de Dados

Crie um banco de dados PostgreSQL chamado:

```
cadastroclientes
```

Em seguida, configure o arquivo:

```
src/main/resources/application.properties
```

Exemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cadastroclientes
spring.datasource.username=postgres
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## Como executar o projeto

1. Clone o repositório:

```bash
git clone https://github.com/luidhz/cadastro-clientes.git
```

2. Abra o projeto no IntelliJ IDEA.

3. Configure o PostgreSQL conforme mostrado acima.

4. Execute a classe:

```
CadastroclientesApplication
```

5. Utilize o Postman para testar os endpoints.

---

## Estrutura do Projeto

```
src
└── main
    └── java
        └── com.luiz.cadastroclientes
            ├── controller
            ├── service
            ├── repository
            ├── entity
            └── CadastroclientesApplication
```
---
## Conceitos praticados

- API REST
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Arquitetura em Camadas
- Injeção de Dependência
- CRUD
- Mapeamento Objeto-Relacional (ORM)
- Requisições HTTP (GET, POST, PUT e DELETE)

---



## Autor

**Luiz Henrique**
