-- Script executado automaticamente pelo container MySQL na primeira inicializacao.
-- O Hibernate (ddl-auto=update) tambem cria/ajusta essa tabela sozinho,
-- mas deixamos aqui para referencia e para popular dados de exemplo.

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS product_manager
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE product_manager;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(60) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- A tabela "users" e criada automaticamente pelo Hibernate (ddl-auto=update)
-- e o usuario admin padrao (admin / admin123) e criado pelo DataSeeder no
-- primeiro start do backend, ja com a senha em hash BCrypt.

INSERT INTO products (name, description, category, price, stock, created_at, updated_at) VALUES
('Notebook Ultra 15', 'Notebook para uso profissional, 16GB RAM, SSD 512GB', 'Eletrônicos', 4599.90, 12, NOW(), NOW()),
('Monitor 27" UltraWide', 'Monitor curvo 27 polegadas, resolução 2K', 'Eletrônicos', 1899.00, 8, NOW(), NOW()),
('Cadeira Ergonômica Office', 'Cadeira com apoio lombar ajustável', 'Mobiliário', 1299.50, 20, NOW(), NOW()),
('Mouse sem fio ProClick', 'Mouse ergonômico com sensor de alta precisão', 'Acessórios', 149.90, 4, NOW(), NOW()),
('Teclado Mecânico Slim', 'Teclado mecânico compacto, switches silenciosos', 'Acessórios', 349.90, 15, NOW(), NOW());
