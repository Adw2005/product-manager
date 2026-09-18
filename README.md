# ProductManager — Sistema de Gestão de Produtos

Trabalho da disciplina **Aplicações para Internet** — desenvolvido em Java com o padrão de
arquitetura **MVC**, incluindo as camadas **DAO** (acesso a dados) e **Service** (regras de
negócio), conforme solicitado no enunciado.

## Stack utilizada

| Camada        | Tecnologia                                |
|---------------|--------------------------------------------|
| Backend       | Java 17 + Spring Boot 3 (MVC, DAO, Service)|
| Frontend      | React + Tailwind CSS                       |
| Banco de dados| MySQL 8                                    |
| Orquestração  | Docker + Docker Compose                    |

## Arquitetura (padrão MVC + DAO + Service)

```
product-manager/
├── backend/
│   ├── src/main/java/com/dmaxtech/productmanager/
│   │   ├── model/          -> Model: entidade Product (JPA)
│   │   ├── repository/     -> DAO: ProductRepository (Spring Data JPA)
│   │   ├── service/        -> Service: regras de negócio (ProductService)
│   │   ├── controller/     -> Controller: rotas REST (ProductController)
│   │   ├── config/         -> Configurações (CORS)
│   │   └── exception/      -> Tratamento de erros
│   └── Dockerfile
├── frontend/                -> View: React + Tailwind (SPA)
│   ├── src/
│   │   ├── api/            -> Comunicação com a API (Axios)
│   │   ├── components/     -> Sidebar, Header, Tabela, Modais
│   │   └── pages/          -> Dashboard
│   └── Dockerfile
├── database/
│   └── init.sql            -> Script de criação da tabela + dados de exemplo
└── docker-compose.yml       -> Orquestra mysql + backend + frontend
```

- **Model** (`Product.java`): representa a entidade Produto e é mapeada para a tabela `products`.
- **DAO** (`ProductRepository.java`): interface Spring Data JPA responsável exclusivamente pelo acesso ao MySQL.
- **Service** (`ProductService.java`): contém as regras de negócio e é o único ponto de acesso ao DAO.
- **Controller** (`ProductController.java`): expõe as rotas REST e mapeia URLs para as ações do sistema.
- **View** (frontend React): consome a API REST e apresenta a interface ao usuário.

## Autenticação

O sistema exige login para acessar o dashboard. A autenticação é feita com **Spring
Security + JWT**:

- `POST /api/auth/login` é a única rota pública da API; todas as demais (`/api/products/**`)
  exigem um token JWT válido no header `Authorization: Bearer <token>`.
- As senhas são armazenadas no MySQL sempre com hash **BCrypt** (nunca em texto puro).
- Ao subir o backend pela primeira vez, um usuário administrador padrão é criado
  automaticamente pelo `DataSeeder`:

  ```
  usuário: admin
  senha:   admin123
  ```

- O token fica salvo no `localStorage` do navegador e é anexado automaticamente em
  toda requisição pelo interceptor do Axios (`src/api/http.js`). Se o token expirar,
  a próxima chamada à API recebe `401` e o usuário é redirecionado para o login.

## CRUD implementado

Entidade **Produto**, com as operações completas:

| Operação | Rota                       | Descrição                       |
|----------|-----------------------------|----------------------------------|
| Create   | `POST /api/products`        | Cadastra um novo produto        |
| Read     | `GET /api/products`         | Lista todos os produtos         |
| Read     | `GET /api/products?search=` | Busca produtos por nome         |
| Read     | `GET /api/products/{id}`    | Busca um produto por id         |
| Update   | `PUT /api/products/{id}`    | Atualiza um produto existente   |
| Delete   | `DELETE /api/products/{id}` | Remove um produto               |

## Como rodar com Docker (recomendado)

Pré-requisitos: Docker e Docker Compose instalados.

```bash
# na raiz do projeto
docker compose up --build
```

Isso sobe 3 containers:
- **MySQL** na porta `3306` (banco `product_manager`, já populado com dados de exemplo)
- **Backend (Spring Boot)** na porta `8080`
- **Frontend (React)** na porta `3000`

Depois de subir, acesse:
- Frontend: http://localhost:3000
- API: http://localhost:8080/api/products

Para parar:
```bash
docker compose down
```

Para apagar também os dados do banco:
```bash
docker compose down -v
```

## Como rodar sem Docker (desenvolvimento local)

### Backend
```bash
cd backend
# configure um MySQL local e ajuste as credenciais em application.properties
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
cp .env.example .env
npm start
```

## Autores

Trabalho em dupla — disciplina Aplicações para Internet.
- Aluno 1: David Alex Moura dos Santos
- Aluno 2: _(completar com o nome do parceiro de dupla)_

## Checklist do enunciado

- [x] Aplicativo web em Java com padrão MVC
- [x] Camada DAO (Data Access Object) — `ProductRepository`
- [x] Camada Service — `ProductService`
- [x] CRUD completo (Create, Read, Update, Delete)
- [x] Servidor local + banco de dados MySQL
- [x] Estrutura em Models, Views e Controllers + sistema de rotas
- [x] Views dinâmicas e interativas (HTML/CSS/JS via React + Tailwind)
- [x] Autenticação (Spring Security + JWT, senha com hash BCrypt)
- [x] README detalhado
- [ ] Repositório no GitHub com commits de ambos os integrantes da dupla (a fazer após criar o repo)
