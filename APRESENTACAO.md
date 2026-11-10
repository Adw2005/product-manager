# APRESENTACAO — ProductManager (Java Puro & Frontend Puro)

> **Disciplina:** Aplicações para Internet  
> **Tema:** Sistema Completo de Gestão de Catálogo e Estoque de Produtos  
> **Backend:** Java Puro (Vanilla Java), sem frameworks (sem Spring Boot, sem Hibernate/JPA)  
> **Servidor Web:** `com.sun.net.httpserver.HttpServer` nativo do Java SE  
> **Persistência:** MySQL com JDBC puro (`Connection`, `PreparedStatement`, `ResultSet`)  
> **Arquitetura:** Padrão MVC com divisão obrigatória em camadas: **Controller**, **Service** e **DAO**  
> **Frontend:** HTML5, CSS3 e JavaScript Vanilla (100% puro, sem frameworks)

---

## 1. O que é a API e qual é o seu Papel no Sistema?

A **API RESTful** desenvolvida é o núcleo de processamento do sistema **ProductManager**.

### Por que a API foi criada e o que ela faz?
1. **Desacoplamento e Segurança:**
   * O navegador (frontend) **nunca** acessa o banco de dados MySQL diretamente, pois isso exporia senhas do banco e permitiria ataques graves como injeção de SQL ou adulteração de regras de negócio.
   * A API atua como uma barreira segura e inteligente: recebe requisições HTTP enviadas pelo frontend, valida as permissões (via token de autenticação), checa as regras de negócio (como impedir preços ou estoques negativos) e traduz essas operações em comandos SQL via **JDBC puro**.
2. **Operações Centralizadas:**
   * **Autenticação:** Validação de login (`admin / admin123`) com senhas em hash **BCrypt** e emissão de tokens assinados com **HMAC-SHA256**.
   * **CRUD Completo:** Cadastrar, listar, buscar por termo, buscar por ID, atualizar dados e excluir produtos do catálogo.
   * **Cálculo em Tempo Real:** Fornecimento dos dados brutos para que o dashboard calcule estatísticas em tempo real (total de itens, quantidade de categorias, produtos com estoque crítico e patrimônio total em estoque).

### Como ela roda nos containers Docker e localmente?
No arquivo `docker-compose.yml`, o sistema é orquestrado em 3 serviços independentes que se comunicam através de uma rede interna (`product-manager-net`):
1. **Container `mysql`:** Roda o servidor MySQL 8 na porta `3306` com os dados persistidos em um volume Docker.
2. **Container `backend`:** Constrói e roda nossa aplicação Java puro na porta `8080`, conectando-se ao MySQL via JDBC.
3. **Container `frontend`:** Roda um servidor estático Nginx na porta `3000` servindo os arquivos HTML5/CSS3/JS puro.
*Além disso, para facilitar o desenvolvimento local sem Docker, o próprio servidor HTTP nativo da aplicação Java serve os arquivos estáticos diretamente na porta `8080` (em `http://localhost:8080/`), permitindo executar todo o ecossistema com um único comando.*

---

## 2. Entidade Principal e Modelagem do Banco

### Entidade Principal: `Product` (Tabela `products`)
| Campo | Tipo no MySQL | Tipo em Java | Regras e Validações |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT AUTO_INCREMENT PRIMARY KEY` | `Long` | Chave primária gerada automaticamente pelo banco |
| `name` | `VARCHAR(120) NOT NULL` | `String` | Nome do produto (obrigatório, máx 120 caracteres) |
| `description` | `VARCHAR(500)` | `String` | Descrição e detalhes (opcional, máx 500 caracteres) |
| `category` | `VARCHAR(60) NOT NULL` | `String` | Categoria do produto (obrigatória, máx 60 caracteres) |
| `price` | `DECIMAL(10,2) NOT NULL` | `BigDecimal` | Preço unitário (obrigatório, não negativo) |
| `stock` | `INT NOT NULL` | `Integer` | Quantidade em estoque (obrigatório, inteiro $\ge 0$) |
| `created_at` | `DATETIME` | `LocalDateTime` | Data/hora de inclusão no banco |
| `updated_at` | `DATETIME` | `LocalDateTime` | Data/hora da última alteração |

### Entidade de Segurança: `User` (Tabela `users`)
Responsável pelos operadores autorizados do sistema (`id`, `username`, `password`, `role`).
As senhas nunca são armazenadas em texto plano: utilizam o algoritmo de derivação com salt **BCrypt** (`$2a$10$...`).

---

## 3. Arquitetura MVC com Separação Obrigatória em Camadas

A arquitetura foi implementada em **Java puro**, sem o uso de Spring Boot, Hibernate ou Jakarta EE, demonstrando o domínio dos conceitos fundamentais da orientação a objetos e da computação web:

```
              [ Navegador / Frontend Puro (HTML/CSS/JS) ]
                                   │
                           (Requisição HTTP)
                                   ▼
 ┌──────────────────────────────────────────────────────────────────┐
 │                     CAMADA CONTROLLER (Web)                      │
 │      • ProductController.java       • AuthController.java        │
 │  - Implementa HttpHandler nativo do Java SE (com.sun.net...)     │
 │  - Mapeia rotas e verbos HTTP (GET, POST, PUT, DELETE, OPTIONS)  │
 │  - Valida token de autenticação Bearer                           │
 │  - Converte JSON (via Gson) e envia respostas com status semântico│
 └─────────────────────────────────┬────────────────────────────────┘
                                   │ (Entidades / DTOs)
                                   ▼
 ┌──────────────────────────────────────────────────────────────────┐
 │                    CAMADA SERVICE (Negócio)                      │
 │       • ProductService.java          • AuthService.java          │
 │  - Aplica as regras de negócio e validações de integridade       │
 │  - Sanitiza strings e previne valores inconsistentes             │
 │  - Lança exceções de domínio (ValidationException, etc.)         │
 │  - É o ÚNICO ponto autorizado a se comunicar com a camada DAO    │
 └─────────────────────────────────┬────────────────────────────────┘
                                   │ (Objetos de Domínio)
                                   ▼
 ┌──────────────────────────────────────────────────────────────────┐
 │                   CAMADA DAO (Acesso a Dados)                    │
 │       • ProductDAO.java             • ProductDAOImpl.java        │
 │       • UserDAO.java                • UserDAOImpl.java           │
 │  - Isolamento completo do acesso ao banco de dados MySQL         │
 │  - Implementação com JDBC puro (Connection, PreparedStatement)   │
 │  - Mapeamento explícito de ResultSets para objetos Java          │
 │  - Fechamento seguro de recursos via try-with-resources          │
 └─────────────────────────────────┬────────────────────────────────┘
                                   │ (SQL via Driver JDBC)
                                   ▼
 ┌──────────────────────────────────────────────────────────────────┐
 │                       BANCO DE DADOS (MySQL)                     │
 │                 Tabelas: "products" e "users"                    │
 └──────────────────────────────────────────────────────────────────┘
```

### 1. Camada Controller (`com.dmaxtech.productmanager.controller`)
* **`ProductController`**: Gerencia o ciclo de vida HTTP do CRUD em `/api/products`. Extrai o token do cabeçalho `Authorization: Bearer <token>`, valida a sessão, analisa o verbo HTTP e despacha a requisição para o serviço correto.
* **`AuthController`**: Gerencia a rota pública `/api/auth/login`, recebendo as credenciais em JSON e respondendo com o token assinado.
* **`StaticFileController`**: Serve de forma autônoma o frontend puro (HTML, CSS e JS) sem necessidade de servidores externos.

### 2. Camada Service (`com.dmaxtech.productmanager.service`)
* **`ProductService`**: Responsável pelas validações de negócio:
  * Garante que nome e categoria não sejam vazios ou ultrapassem o limite de caracteres.
  * Valida que o preço seja um valor numérico positivo ou zero.
  * Valida que a quantidade em estoque não seja negativa.
  * Lança `ValidationException` (que o Controller traduz em HTTP 400 com os campos incorretos) e `ResourceNotFoundException` (HTTP 404).
* **`AuthService`**: Verifica senhas com `BCrypt.checkpw` e assina digitalmente tokens criptográficos com **HMAC-SHA256**.

### 3. Camada DAO (`com.dmaxtech.productmanager.dao`)
* **`ProductDAO`** (Interface) e **`ProductDAOImpl`** (Classe com JDBC puro):
  * `findAll()`: `SELECT * FROM products ORDER BY id ASC`
  * `findById(id)`: `SELECT * FROM products WHERE id = ?`
  * `searchByName(name)`: `SELECT * FROM products WHERE UPPER(name) LIKE UPPER(?)`
  * `save(product)`: `INSERT INTO products (...) VALUES (...)` obtendo o ID gerado via `Statement.RETURN_GENERATED_KEYS`
  * `update(product)`: `UPDATE products SET ... WHERE id = ?`
  * `deleteById(id)`: `DELETE FROM products WHERE id = ?`

---

## 4. Endpoints da API RESTful

| Verbo | Rota | Autenticação | Descrição | Status Sucesso |
| :---: | :--- | :---: | :--- | :---: |
| `POST` | `/api/auth/login` | Pública | Autentica usuário e retorna token HMAC-SHA256 | `200 OK` |
| `GET` | `/api/products` | Bearer Token | Lista todos os produtos | `200 OK` |
| `GET` | `/api/products?search={nome}` | Bearer Token | Filtra produtos por busca de texto | `200 OK` |
| `GET` | `/api/products/{id}` | Bearer Token | Consulta detalhes do produto por ID | `200 OK` |
| `POST` | `/api/products` | Bearer Token | Cadastra um novo produto no banco | `201 Created` |
| `PUT` | `/api/products/{id}` | Bearer Token | Atualiza dados de um produto existente | `200 OK` |
| `DELETE` | `/api/products/{id}` | Bearer Token | Exclui permanentemente um produto | `204 No Content` |
| `GET` | `/docs.html` | Pública | Documentação visual das rotas da API | `200 OK` |

---

## 5. Roteiro de Demonstração para a Banca / Professor (5 a 8 minutos)

1. **Abertura (1 minuto):**
   * Apresentar o nome do projeto (**ProductManager**), integrantes da dupla e contextualizar: sistema desenvolvido em **Java Puro**, sem frameworks como Spring Boot, utilizando o **HttpServer nativo** e persistência **JDBC pura** no MySQL.
2. **Demonstração do Sistema Web e Autenticação (2 minutos):**
   * Acessar `http://localhost:8080/`.
   * Tentar login com senha incorreta para demonstrar o tratamento de erro `401 Unauthorized`.
   * Realizar login com as credenciais padrão: `admin` / `admin123`.
   * Mostrar o Dashboard, os 4 cards de métricas dinâmicas e o carregamento dos dados do MySQL.
3. **Demonstração do CRUD Completo (2 minutos):**
   * **Cadastrar:** Clicar em "Novo Produto", cadastrar um novo item e verificar a atualização instantânea.
   * **Buscar:** Digitar no campo de busca para demonstrar o filtro em tempo real.
   * **Editar:** Clicar em editar, alterar o estoque ou preço e salvar.
   * **Excluir:** Abrir o modal de confirmação e excluir o produto.
4. **Demonstração do Código Fonte em Camadas (2 minutos):**
   * Mostrar a classe `ProductController.java` (HttpHandler nativo, leitura de métodos e headers).
   * Mostrar a classe `ProductService.java` (regras de validação).
   * Mostrar a classe `ProductDAOImpl.java` (comandos SQL preparados com `PreparedStatement` e mapeamento de `ResultSet`).
5. **Encerramento (1 minuto):**
   * Abrir a página `http://localhost:8080/docs.html` com a documentação dos endpoints e frisar que a aplicação cumpre 100% dos requisitos sem depender de nenhum framework.
