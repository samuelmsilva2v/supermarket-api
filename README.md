# API Supermercado
![GitHub repo size](https://img.shields.io/github/repo-size/samuelmsilva2v/supermarket-api?style=for-the-badge)
![GitHub language count](https://img.shields.io/github/languages/count/samuelmsilva2v/supermarket-api?style=for-the-badge)
![GitHub forks](https://img.shields.io/github/forks/samuelmsilva2v/supermarket-api?style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues/samuelmsilva2v/supermarket-api?style=for-the-badge)
![GitHub pull requests](https://img.shields.io/github/issues-pr/samuelmsilva2v/supermarket-api?style=for-the-badge)

[🇺🇸 Read in English](#supermarket-api)

🛒 API RESTful desenvolvida em **Java** e **Spring Boot** para controle e gerenciamento de produtos de um supermercado, permitindo operações de CRUD (Create, Read, Update, Delete).

O sistema garante que as regras de negócio sejam seguidas, como a obrigatoriedade de categorias, prevenção de cadastro duplicado e restrições na exclusão de produtos com estoque.

## Funcionalidades
* Cadastrar, editar, excluir e consultar produtos (por ID, listagem completa ou busca paginada por nome, preço, quantidade, unidade de medida e categoria).

* Cadastrar, editar, excluir e consultar categorias (por ID, listagem completa ou busca paginada por nome).

* Registrar movimentações de estoque (entrada/saída) e consultar o histórico paginado por produto.

* Apresentação de um dashboard com gráfico para consulta de produtos cadastrados por categoria.

* Criação de usuários (restrita a administradores), autenticação, consulta paginada com filtros, edição e ativação/inativação (todas restritas a administradores).

* Autoatendimento: qualquer usuário autenticado pode consultar e editar o próprio nome/sobrenome/e-mail e trocar a própria senha, sem depender de um administrador.

* Recuperação de senha: o usuário informa o e-mail cadastrado e recebe uma nova senha gerada automaticamente, enviada por e-mail através de uma fila de mensageria (RabbitMQ).

### Regras de negócio
* Não é possível cadastrar produtos ou categorias com o mesmo nome.

* Não é possível excluir um produto que tenha estoque maior que 0, nem uma categoria que ainda tenha produtos associados a ela.

* O preço do produto não pode ser negativo, e é obrigatório informar uma categoria ao cadastrar ou editar um produto.

* Uma movimentação de saída não pode ultrapassar o estoque disponível do produto.

* Apenas um usuário autenticado pode acessar a API — exceto os serviços de login e recuperação de senha, que são públicos.

* Não é possível cadastrar um usuário com um e-mail ou username já utilizado.

* A senha do usuário deve conter letras maiúsculas, minúsculas, números e símbolos, com no mínimo 8 caracteres.

* Criar, consultar, editar e ativar/inativar usuários são ações restritas a administradores; o autoatendimento (editar o próprio perfil e trocar a própria senha) é liberado para qualquer usuário autenticado.

* A recuperação de senha exige que o e-mail informado exista na base — a API retorna um erro caso contrário.

## Tecnologias Utilizadas
#### Back-end:
* Java 21
* Spring Boot
* Spring Data JPA
* Filtro JWT customizado (`GenericFilterBean` próprio, sem Spring Security)
* Spring Security Crypto (BCrypt, para criptografia de senhas)
* Hibernate
* PostgreSQL (produtos, categorias, usuários e movimentações de estoque)
* RabbitMQ (mensageria assíncrona para o fluxo de recuperação de senha)
* Spring Mail / JavaMailSender (envio de e-mail; em desenvolvimento local passa pelo Mailpit, sem SMTP real)
* Docker / Docker Compose (Postgres, RabbitMQ e Mailpit)
* JUnit (para testes unitários)
* Swagger (para documentação)
#### Front-end (Web):
* Angular 19
* Bootstrap
* HttpClient (integração com back-end)
* Angular Highcharts (construção do dashboard)
* Angular Guards (para controle de acesso)

## Endpoints

- #### Produtos
| Método | Endpoint                | Descrição                                                                          |
|--------|--------------------------|-------------------------------------------------------------------------------------|
| POST   | `/api/produtos`          | Cadastra um novo produto                                                            |
| PUT    | `/api/produtos/{id}`     | Edita os dados de um produto                                                        |
| DELETE | `/api/produtos/{id}`     | Exclui um produto                                                                    |
| GET    | `/api/produtos/{id}`     | Consulta um produto através do ID                                                   |
| GET    | `/api/produtos`          | Consulta todos os produtos                                                          |
| GET    | `/api/produtos/consultar`| Consulta produtos por filtros (nome, preço, quantidade, unidade de medida, categoria), paginado |

- #### Categorias
| Método | Endpoint                  | Descrição                                          |
|--------|----------------------------|-----------------------------------------------------|
| POST   | `/api/categorias`         | Cadastra uma nova categoria                          |
| PUT    | `/api/categorias/{id}`    | Edita os dados de uma categoria                      |
| DELETE | `/api/categorias/{id}`    | Exclui uma categoria                                 |
| GET    | `/api/categorias/{id}`    | Consulta uma categoria através do ID                 |
| GET    | `/api/categorias`         | Consulta todas as categorias                         |
| GET    | `/api/categorias/consultar`| Consulta categorias por nome, paginado              |

- #### Movimentação de estoque
| Método | Endpoint                                        | Descrição                                                  |
|--------|--------------------------------------------------|--------------------------------------------------------------|
| POST   | `/api/movimentacoes-estoque`                    | Registra uma movimentação de estoque (entrada ou saída)      |
| GET    | `/api/movimentacoes-estoque/produto/{produtoId}`| Consulta o histórico de movimentações de um produto, paginado |

- #### Dashboard
| Método | Endpoint                           | Descrição                                     |
|--------|-------------------------------------|-------------------------------------------------|
| GET    | `/api/dashboard/produtos-categoria`| Consulta quantidade de produtos por categoria    |

- #### Usuário / Autenticação
| Método | Endpoint                    | Descrição                                                                |
|--------|------------------------------|-----------------------------------------------------------------------------|
| POST   | `/api/usuario/criar`        | Cadastra um novo usuário *(admin)*                                          |
| POST   | `/api/usuario/autenticar`   | Autentica um usuário e retorna um token                                     |
| POST   | `/api/usuario/esqueci-senha`| Gera uma nova senha e envia por e-mail                                      |
| GET    | `/api/usuario`              | Consulta usuários por filtros (username, nome, status, perfil), paginado *(admin)* |
| GET    | `/api/usuario/{id}`         | Consulta um usuário através do ID *(admin)*                                 |
| PUT    | `/api/usuario/{id}`         | Edita um usuário *(admin)*                                                  |
| PATCH  | `/api/usuario/{id}/status`  | Ativa/inativa um usuário *(admin)*                                          |
| GET    | `/api/usuario/me`           | Consulta os dados do próprio usuário autenticado                            |
| PUT    | `/api/usuario/me`           | Edita nome, sobrenome e e-mail do próprio usuário autenticado               |
| PUT    | `/api/usuario/me/senha`     | Troca a própria senha, mediante confirmação da senha atual                  |

> Todos os endpoints acima, exceto `autenticar` e `esqueci-senha`, exigem um cabeçalho `Authorization: Bearer <token>`. Os marcados como *(admin)* exigem, além disso, que o token pertença a um usuário com perfil `Administrador`.

## Instalação e Configuração

### Pré-requisitos
- Docker e Docker Compose
- Java 21
- Spring Boot
- Angular CLI
- pgAdmin (opcional, para inspecionar o banco PostgreSQL)

**1. Clonar o Repositório do Back-end da Supermarket API**
```bash
 git clone https://github.com/samuelmsilva2v/supermarket-api.git
 cd supermarket-api
```

**2. Subir a infraestrutura local no Docker**
```bash
docker-compose up -d
```
Isso sobe três serviços:
- **PostgreSQL** (produtos, categorias, usuários e movimentações de estoque)
- **RabbitMQ** — fila usada pelo fluxo de recuperação de senha; console de administração em http://localhost:15672 (usuário/senha padrão: `guest`/`guest`)
- **Mailpit** — captura os e-mails "enviados" localmente, sem precisar de um SMTP real; visualize-os em http://localhost:8025

**3. Executar o Back-end da Supermarket API**
```bash
mvn spring-boot:run
```
A Supermarket API — incluindo cadastro, login, autenticação e recuperação de senha, que agora fazem parte da própria API — estará disponível em http://localhost:8080/swagger-ui/index.html#/.

Na primeira execução um usuário administrador padrão é criado automaticamente (configurável em `application.properties`, propriedades `app.admin.*`):
- **Username:** `admin`
- **Senha:** `Admin@123`

> Essas são credenciais de desenvolvimento/estudo. Troque-as antes de qualquer uso além de testes locais.

**4. Clonar o Repositório do Front-end**
```bash
 git clone https://github.com/samuelmsilva2v/supermarket-web.git
 cd supermarket-web
```

**5. Instale as dependências do projeto:**
```bash
npm install
```

**6. Executar o front-end:**
```bash
$ ng s -o 
```
Isso irá iniciar o servidor de desenvolvimento na URL http://localhost:4200/. Você pode abrir seu navegador e acessar essa URL para visualizar a aplicação.

### Testes
Para rodar os testes automatizados
```bash
mvn test
```

---

# Supermarket API
[🇧🇷 Leia em Português](#api-supermercado)

🛒 RESTful API developed in Java and Spring Boot for controlling and managing supermarket products, allowing CRUD operations (Create, Read, Update, Delete).

The system ensures that business rules are followed, such as mandatory categories, prevention of duplicate registrations, and restrictions on deleting products with stock.

## Features
* Create, edit, delete and query products (by ID, full listing, or paginated search by name, price, quantity, unit of measure and category).

* Create, edit, delete and query categories (by ID, full listing, or paginated search by name).

* Register stock movements (in/out) and query the paginated movement history per product.

* Presentation of a dashboard with a chart for querying registered products by category.

* User creation (admin-only), authentication, paginated search with filters, editing and activation/deactivation (all admin-only).

* Self-service: any authenticated user can view and edit their own name/surname/email, and change their own password, without needing an administrator.

* Password recovery: the user provides their registered e-mail and receives a newly generated password by e-mail, sent through a message queue (RabbitMQ).

### Business Rules
* Products and categories cannot be registered with a duplicate name.

* A product cannot be deleted if its stock is greater than 0, nor can a category be deleted while it still has products associated with it.

* The product price cannot be negative, and a category must be provided when registering or editing a product.

* An outbound stock movement cannot exceed the product's available stock.

* Only an authenticated user can access the API — except for login and password recovery, which are public.

* A user cannot be registered with an e-mail or username that is already in use.

* The user's password must contain uppercase and lowercase letters, numbers and symbols, with at least 8 characters.

* Creating, listing, editing and activating/deactivating users are admin-only actions; self-service (editing your own profile and changing your own password) is available to any authenticated user.

* Password recovery requires the provided e-mail to exist in the database — the API returns an error otherwise.

## Technologies Used
### Back-end:
* Java 21
* Spring Boot
* Spring Data JPA
* Custom JWT filter (a plain `GenericFilterBean`, not Spring Security)
* Spring Security Crypto (BCrypt password hashing)
* Hibernate
* PostgreSQL (products, categories, users and stock movements)
* RabbitMQ (asynchronous messaging for the password-recovery flow)
* Spring Mail / JavaMailSender (e-mail sending; in local development this goes through Mailpit, no real SMTP required)
* Docker / Docker Compose (Postgres, RabbitMQ and Mailpit)
* JUnit (for unit testing)
* Swagger (for documentation)
#### Front-end:
* Angular 19
* Bootstrap
* HttpClient (integration with back-end)
* Angular Highcharts (dashboard construction)
* Angular Guards

## Endpoints

- #### Products
| Method | Endpoint                 | Description                                                                    |
|--------|----------------------------|----------------------------------------------------------------------------------|
| POST   | `/api/produtos`           | Registers a new product                                                          |
| PUT    | `/api/produtos/{id}`      | Edits product data                                                               |
| DELETE | `/api/produtos/{id}`      | Deletes a product                                                                |
| GET    | `/api/produtos/{id}`      | Retrieves a product by ID                                                        |
| GET    | `/api/produtos`           | Retrieves all products                                                          |
| GET    | `/api/produtos/consultar` | Retrieves products by filters (name, price, quantity, unit of measure, category), paginated |

- #### Categories
| Method | Endpoint                    | Description                                    |
|--------|-------------------------------|---------------------------------------------------|
| POST   | `/api/categorias`            | Registers a new category                           |
| PUT    | `/api/categorias/{id}`       | Edits category data                                |
| DELETE | `/api/categorias/{id}`       | Deletes a category                                 |
| GET    | `/api/categorias/{id}`       | Retrieves a category by ID                         |
| GET    | `/api/categorias`            | Retrieves all categories                           |
| GET    | `/api/categorias/consultar`  | Retrieves categories by name, paginated            |

- #### Stock movements
| Method | Endpoint                                         | Description                                        |
|--------|----------------------------------------------------|-------------------------------------------------------|
| POST   | `/api/movimentacoes-estoque`                      | Registers a stock movement (inbound or outbound)       |
| GET    | `/api/movimentacoes-estoque/produto/{produtoId}` | Retrieves a product's paginated movement history       |

- #### Dashboard
| Method | Endpoint                            | Description                                |
|--------|---------------------------------------|--------------------------------------------|
| GET    | `/api/dashboard/produtos-categoria`  | Query the quantity of products by category |

- #### User / Authentication
| Method | Endpoint                     | Description                                                             |
|--------|--------------------------------|-------------------------------------------------------------------------|
| POST   | `/api/usuario/criar`          | Registers a new user *(admin)*                                          |
| POST   | `/api/usuario/autenticar`     | Authenticates a user and returns a token                                |
| POST   | `/api/usuario/esqueci-senha`  | Generates a new password and sends it by e-mail                         |
| GET    | `/api/usuario`                | Retrieves users by filters (username, name, status, role), paginated *(admin)* |
| GET    | `/api/usuario/{id}`           | Retrieves a user by ID *(admin)*                                        |
| PUT    | `/api/usuario/{id}`           | Edits a user *(admin)*                                                  |
| PATCH  | `/api/usuario/{id}/status`    | Activates/deactivates a user *(admin)*                                  |
| GET    | `/api/usuario/me`             | Retrieves the authenticated user's own data                             |
| PUT    | `/api/usuario/me`             | Edits the authenticated user's own name, surname and e-mail             |
| PUT    | `/api/usuario/me/senha`       | Changes the user's own password, given the current password             |

> Every endpoint above except `autenticar` and `esqueci-senha` requires an `Authorization: Bearer <token>` header. Those marked *(admin)* additionally require the token to belong to a user with the `Administrador` role.

## Installation and Configuration

### Prerequisites
- Docker e Docker Compose
- Java 21
- Spring Boot
- Angular CLI
- pgAdmin (optional, for inspecting the PostgreSQL database)

**1. Clone the Back-end Repository for the Supermarket API**
```bash
 git clone https://github.com/samuelmsilva2v/supermarket-api.git
 cd supermarket-api
```

**2. Start the local infrastructure in Docker**
```bash
docker-compose up -d
```
This starts three services:
- **PostgreSQL** (products, categories, users and stock movements)
- **RabbitMQ** — the queue used by the password-recovery flow; management console at http://localhost:15672 (default user/password: `guest`/`guest`)
- **Mailpit** — captures "sent" e-mails locally without a real SMTP server; view them at http://localhost:8025

**3. Run the Back-end of the Supermarket API**
```bash
mvn spring-boot:run
```
The Supermarket API — including user registration, login, authentication and password recovery, which are now part of the API itself — will be available at http://localhost:8080/swagger-ui/index.html#/.

On the first run, a default admin user is created automatically (configurable in `application.properties`, `app.admin.*` properties):
- **Username:** `admin`
- **Password:** `Admin@123`

> These are development/study credentials. Change them before any use beyond local testing.

**4. Clone the Front-end Repository**
```bash
git clone https://github.com/samuelmsilva2v/supermarket-web.git
cd supermarket-web
```

**5. Install the project dependencies:**
```bash
npm install
```

**6. Run the front-end:**
```bash
ng s -o
```

This will start the development server at URL http://localhost:4200/. You can open your browser and access this URL to view the application.

### Testing
To run automated tests
```bash
mvn test
```
