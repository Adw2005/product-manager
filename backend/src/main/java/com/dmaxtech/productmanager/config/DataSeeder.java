package com.dmaxtech.productmanager.config;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Inicializador de dados e tabelas executado no início da aplicação.
 * Garante que o esquema MySQL e o usuário padrão de teste existam.
 */
public class DataSeeder {

    public static void seed() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            createTablesIfNotExist(conn);
            seedAdminUser(conn);
            seedInitialProductsIfEmpty(conn);
            System.out.println(">> [DataSeeder] Banco de dados verificado e inicializado com sucesso.");
        } catch (Exception e) {
            System.err.println(">> [DataSeeder] Aviso na inicialização do banco: " + e.getMessage());
        }
    }

    private static void createTablesIfNotExist(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            // Tabela products
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS products (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(120) NOT NULL, " +
                            "description VARCHAR(500), " +
                            "category VARCHAR(60) NOT NULL, " +
                            "price DECIMAL(10,2) NOT NULL, " +
                            "stock INT NOT NULL, " +
                            "created_at DATETIME, " +
                            "updated_at DATETIME" +
                            ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
            );

            // Tabela users
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "username VARCHAR(60) NOT NULL UNIQUE, " +
                            "password VARCHAR(255) NOT NULL, " +
                            "role VARCHAR(20) NOT NULL" +
                            ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
            );
        }
    }

    private static void seedAdminUser(Connection conn) throws Exception {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, "admin");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setString(1, "admin");
                        insertPs.setString(2, BCrypt.hashpw("admin123", BCrypt.gensalt()));
                        insertPs.setString(3, "ADMIN");
                        insertPs.executeUpdate();
                        System.out.println(">> [DataSeeder] Usuário admin padrão criado (admin / admin123).");
                    }
                }
            }
        }
    }

    private static void seedInitialProductsIfEmpty(Connection conn) throws Exception {
        String checkSql = "SELECT COUNT(*) FROM products";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO products (name, description, category, price, stock, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    Object[][] initial = {
                            {"Notebook Ultra 15", "Notebook para uso profissional, 16GB RAM, SSD 512GB", "Eletrônicos", new BigDecimal("4599.90"), 12},
                            {"Monitor 27\" UltraWide", "Monitor curvo 27 polegadas, resolução 2K", "Eletrônicos", new BigDecimal("1899.00"), 8},
                            {"Cadeira Ergonômica Office", "Cadeira com apoio lombar ajustável", "Mobiliário", new BigDecimal("1299.50"), 20},
                            {"Mouse sem fio ProClick", "Mouse ergonômico com sensor de alta precisão", "Acessórios", new BigDecimal("149.90"), 4},
                            {"Teclado Mecânico Slim", "Teclado mecânico compacto, switches silenciosos", "Acessórios", new BigDecimal("349.90"), 15}
                    };

                    for (Object[] item : initial) {
                        ps.setString(1, (String) item[0]);
                        ps.setString(2, (String) item[1]);
                        ps.setString(3, (String) item[2]);
                        ps.setBigDecimal(4, (BigDecimal) item[3]);
                        ps.setInt(5, (Integer) item[4]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    System.out.println(">> [DataSeeder] 5 produtos iniciais cadastrados no estoque.");
                }
            }
        }
    }
}

