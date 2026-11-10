# ProductManager — Sistema de Gestão de Produtos (Java Puro)

Trabalho da disciplina **Aplicações para Internet** — desenvolvido em **Java Puro (Vanilla Java)**, sem utilização de frameworks de orquestração ou persistência (sem Spring Boot, sem Hibernate, sem Spring Data JPA, sem Jakarta EE). 

A aplicação implementa uma **API RESTful completa** utilizando o servidor HTTP nativo do Java (`com.sun.net.httpserver.HttpServer`), o padrão arquitetural **MVC** com divisão obrigatória em camadas (**Controller**, **Service** e **DAO**), persistência no banco de dados **MySQL** através de **JDBC puro** e interface de usuário construída exclusivamente em **HTML5, CSS3 e JavaScript puro (Vanilla JS sem frameworks)**.

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia | Detalhes |
| :--- | :--- | :--- |
| **Linguagem** | Java 17 / 21 | Código limpo em **Java Puro**, sem frameworks |
| **Servidor HTTP** | `com.sun.net.httpserver.HttpServer` | Servidor web nativo do Java SE |
| **Persistência / BD** | JDBC Puro + MySQL 8.x/9.x | `Connection`, `PreparedStatement`, `ResultSet` |
| **Serialização** | Google Gson | Biblioteca minimalista para serialização/desserialização JSON |
| **Criptografia** | BCrypt + HMAC-SHA256 | Senhas com hash BCrypt e tokens assinados nativamente |
| **Frontend** | HTML5 + CSS3 + JavaScript Puro | **100% puro (sem frameworks)**, sem React, sem Tailwind, sem build |
| **Testes** | JUnit 5 + Mockito | Testes unitários da camada de serviço |
| **Orquestração** | Docker + Docker Compose | MySQL + Backend Java Puro + Frontend Nginx |

---

## 🏛️ Arquitetura do Projeto (MVC + DAO + Service)

```
product-manager/
├── backend/
│   ├── src/main/java/com/dmaxtech/productmanager/
│   │   ├── config/         -> Conexão JDBC (DatabaseConfig) e Seeder de dados (DataSeeder)
│   │   ├── controller/     -> Camada CONTROLLER (ProductController, AuthController, StaticFileController)
│   │   ├── dao/            -> Camada DAO (ProductDAO, ProductDAOImpl, UserDAO, UserDAOImpl)
│   │   ├── dto/            -> Transferência de dados (LoginRequest, LoginResponse, ErrorResponse)
│   │   ├── exception/      -> Exceções de negócio (ValidationException, ResourceNotFoundException)
│   │   ├── model/          -> Camada MODEL (entidades POJO puras: Product, User)
│   │   ├── service/        -> Camada SERVICE (regras de negócio e validações: ProductService, AuthService)
│   │   ├── util/           -> Utilitários JSON (JsonUtil via Gson) e HTTP (HttpUtil)
│   │   └── ProductManagerApplication.java -> Main class e inicialização do HttpServer nativo
│   ├── src/main/resources/
│   │   └── static/         -> Frontend puro embutido e servido pelo backend
│   │       ├── css/style.css
│   │       ├── js/api.js
│   │       ├── js/app.js
│   │       ├── docs.html
│   │       └── index.html
│   ├── pom.xml             -> Dependências Maven (JDBC Driver, Gson, BCrypt, JUnit)
│   ├── mvnw / mvnw.cmd     -> Maven Wrapper integrado
│   └── Dockerfile          -> Container multi-stage Java Puro
├── frontend/               -> Frontend Puro para servidor estático Nginx / Docker
│   ├── css/style.css       -> Estilização moderna responsiva (zero frameworks)
│   ├── js/api.js           -> Cliente REST nativo com Fetch API
│   ├── js/app.js           -> Lógica de DOM, busca e modais
│   ├── docs.html           -> Documentação visual dos endpoints
│   ├── index.html          -> Estrutura semântica HTML5
│   ├── nginx.conf          -> Configuração do Nginx
│   └── Dockerfile          -> Imagem Nginx Alpine leve
├── database/
│   └── init.sql            -> Script DDL de criação e população inicial
├── docker-compose.yml       -> Orquestra mysql + backend + frontend
├── APRESENTACAO.md          -> Guia resumido da arquitetura e roteiro de apresentação
└── README.md
```

### Responsabilidade Detalhada das Camadas

1. **Model (`com.dmaxtech.productmanager.model`):**
   * Objetos Java puros (POJOs) que representam as entidades de negócio (`Product` e `User`). Não contêm anotações de ORMs ou frameworks externos.
2. **DAO — Data Access Object (`com.dmaxtech.productmanager.dao`):**
   * Isolamento total do acesso ao banco de dados MySQL.
   * `ProductDAO` define o contrato CRUD e `ProductDAOImpl` executa comandos SQL preparados via `PreparedStatement`, mapeando `ResultSet` para entidades e fechando conexões com `try-with-resources`.
3. **Service (`com.dmaxtech.productmanager.service`):**
   * Contém as regras de negócio e validações (preço positivo, estoque não negativo, campos obrigatórios preenchidos).
   * Lança exceções semânticas (`ValidationException`, `ResourceNotFoundException`). É o **único ponto de acesso autorizado ao DAO**.
4. **Controller (`com.dmaxtech.productmanager.controller`):**
   * Implementa `com.sun.net.httpserver.HttpHandler` do Java SE.
   * Valida o token Bearer no cabeçalho `Authorization`, processa o verbo HTTP correspondente, lê e escreve JSON e define os códigos de status HTTP apropriados.

---

## 🔒 Autenticação e Segurança

- A rota `POST /api/auth/login` é pública e recebe usuário e senha.
- Senhas são comparadas utilizando o algoritmo seguro **BCrypt** (`BCrypt.checkpw`).
- O sistema emite um token assinado criptograficamente com **HMAC-SHA256**.
- Todas as rotas de produtos (`/api/products/**`) exigem o token no cabeçalho:
  ```http
  Authorization: Bearer <token>
  ```
- O `DataSeeder` garante que o usuário administrador de teste exista no MySQL:
  - **Usuário:** `admin`
  - **Senha:** `admin123`

---

## 📋 Endpoints da API RESTful (CRUD Completo)

| Operação | Método | Rota | Descrição | Status Sucesso |
| :--- | :---: | :--- | :--- | :---: |
| **Login** | `POST` | `/api/auth/login` | Autentica e gera token HMAC-SHA256 | `200 OK` |
| **Listar** | `GET` | `/api/products` | Lista todos os produtos cadastrados | `200 OK` |
| **Buscar** | `GET` | `/api/products?search={termo}` | Filtra produtos pelo nome | `200 OK` |
| **Consultar** | `GET` | `/api/products/{id}` | Busca produto específico por ID | `200 OK` |
| **Cadastrar** | `POST` | `/api/products` | Cadastra um novo produto no banco | `201 Created` |
| **Atualizar** | `PUT` | `/api/products/{id}` | Atualiza os dados de um produto | `200 OK` |
| **Excluir** | `DELETE` | `/api/products/{id}` | Remove um produto do banco | `204 No Content` |
| **Docs** | `GET` | `/docs.html` | Página visual com a documentação da API | `200 OK` |

### Exemplos de Requisição

#### 1. Login (`POST /api/auth/login`)
```json
{
  "username": "admin",
  "password": "admin123"
}
```
**Resposta (200 OK):**
```json
{
  "token": "YWRtaW46MTc5NDM2MDI2ODEzNA.oYD8rS2...",
  "username": "admin",
  "role": "ADMIN"
}
```

#### 2. Cadastrar Produto (`POST /api/products`)
*Header:* `Authorization: Bearer <token>`
```json
{
  "name": "Monitor 27\" UltraWide",
  "description": "Monitor curvo 27 polegadas, resolução 2K",
  "category": "Eletrônicos",
  "price": 1899.00,
  "stock": 8
}
```
**Resposta (201 Created):**
```json
{
  "id": 2,
  "name": "Monitor 27\" UltraWide",
  "description": "Monitor curvo 27 polegadas, resolução 2K",
  "category": "Eletrônicos",
  "price": 1899.00,
  "stock": 8,
  "createdAt": "2026-11-09T22:24:27",
  "updatedAt": "2026-11-09T22:24:27"
}
```

---

## 🚀 Como Executar o Projeto

### Opção 1: Execução Local Rápida (sem Docker)

#### 1. Iniciar o Banco MySQL
Certifique-se de que o MySQL está rodando na porta `3306`. Crie o banco `product_manager` e a tabela inicial:
```bash
mysql -u root -p < database/init.sql
```
*(Se estiver utilizando WAMP/XAMPP, o usuário padrão é `root` sem senha).*

#### 2. Compilar e Iniciar o Backend Java Puro
Abra o terminal na pasta `backend`:
```bash
cd backend

# No Windows (usando o Maven Wrapper embutido):
.\mvnw.cmd package -DskipTests
java -jar target\product-manager-1.0.0.jar

# No Linux / macOS:
./mvnw package -DskipTests
java -jar target/product-manager-1.0.0.jar
```

#### 3. Acessar o Sistema
Abra seu navegador:
* **Frontend Puro:** [http://localhost:8080/](http://localhost:8080/)
* **Documentação da API:** [http://localhost:8080/docs.html](http://localhost:8080/docs.html)

---

### Opção 2: Execução com Docker

Pré-requisito: Docker Desktop ou Docker Engine instalado.

```bash
docker compose up --build
```

Isso sobe os 3 serviços:
1. **MySQL 8.0** na porta `3306` (banco `product_manager`).
2. **Backend Java Puro** na porta `8080`.
3. **Frontend Nginx Puro** na porta `3000`.

**Acessos:**
* Frontend Nginx: [http://localhost:3000/](http://localhost:3000/)
* Frontend Java: [http://localhost:8080/](http://localhost:8080/)
* Documentação da API: [http://localhost:8080/docs.html](http://localhost:8080/docs.html)

Para parar:
```bash
docker compose down
```

---

## 🧪 Execução dos Testes Automatizados

Os testes unitários cobrem a camada de serviço e suas validações:
```bash
cd backend
.\mvnw.cmd test
```

Resultado esperado:
```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## ✅ Checklist dos Requisitos Atendidos

- [x] **Java Puro (Vanilla Java), sem frameworks (sem Spring Boot, sem Hibernate)**
- [x] **Padrão arquitetural MVC completo**
- [x] **Separação obrigatória em camadas: Controller, Service e DAO**
- [x] **Banco de dados MySQL conectado e persistido**
- [x] **Conexão e manipulação do banco via JDBC puro (`Connection`, `PreparedStatement`, `ResultSet`)**
- [x] **CRUD completo da entidade principal Produto (Create, Read, Update, Delete)**
- [x] **Servidor HTTP nativo com `com.sun.net.httpserver.HttpServer`**
- [x] **Validação rigorosa de dados e tratamento de exceções semântico**
- [x] **Frontend migrado 100% para HTML5, CSS3 e JavaScript puro (sem frameworks)**
- [x] **Autenticação segura (BCrypt + HMAC-SHA256)**
- [x] **Zero hardcoded: credenciais configuráveis via variáveis de ambiente com fallbacks seguros**
- [x] **Documentação das rotas (`docs.html` e `README.md`) e guia em `APRESENTACAO.md`**
