package com.dmaxtech.productmanager;

import com.dmaxtech.productmanager.config.DataSeeder;
import com.dmaxtech.productmanager.config.DatabaseConfig;
import com.dmaxtech.productmanager.controller.AuthController;
import com.dmaxtech.productmanager.controller.ProductController;
import com.dmaxtech.productmanager.controller.StaticFileController;
import com.dmaxtech.productmanager.dao.ProductDAO;
import com.dmaxtech.productmanager.dao.ProductDAOImpl;
import com.dmaxtech.productmanager.dao.UserDAO;
import com.dmaxtech.productmanager.dao.UserDAOImpl;
import com.dmaxtech.productmanager.service.AuthService;
import com.dmaxtech.productmanager.service.ProductService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Ponto de entrada da aplicação ProductManager.
 * Desenvolvido em Vanilla Java (Java Puro) sem Spring Boot ou frameworks adicionais.
 * Utiliza o HttpServer nativo do Java SE, JDBC para comunicação com o MySQL
 * e arquitetura MVC com divisão em camadas Controller, Service e DAO.
 */
public class ProductManagerApplication {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  ProductManager — Sistema de Gestão de Produtos (Java Puro)  ");
        System.out.println("  Arquitetura MVC com Camadas Controller, Service e DAO (JDBC) ");
        System.out.println("===============================================================");

        int port = getPort();

        // 1. Inicializa o banco de dados e dados padrão via JDBC
        System.out.println(">> Conectando ao MySQL em: " + DatabaseConfig.getJdbcUrl());
        DataSeeder.seed();

        // 2. Instancia as camadas arquiteturais (Injeção de dependência manual pura)
        ProductDAO productDAO = new ProductDAOImpl();
        UserDAO userDAO = new UserDAOImpl();

        ProductService productService = new ProductService(productDAO);
        AuthService authService = new AuthService(userDAO);

        ProductController productController = new ProductController(productService, authService);
        AuthController authController = new AuthController(authService);
        StaticFileController staticFileController = new StaticFileController();

        // 3. Inicializa o servidor HTTP nativo
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Registro das rotas da API RESTful
            server.createContext("/api/auth/login", authController);
            server.createContext("/api/products", productController);

            // Servidor de arquivos estáticos do frontend puro
            server.createContext("/", staticFileController);

            // Pool de threads nativo para atender múltiplas requisições simultâneas
            server.setExecutor(Executors.newFixedThreadPool(12));

            server.start();

            System.out.println(">> Servidor HTTP nativo iniciado com sucesso na porta: " + port);
            System.out.println(">> Frontend Puro: http://localhost:" + port + "/");
            System.out.println(">> API Produtos:  http://localhost:" + port + "/api/products");
            System.out.println(">> API Auth:      http://localhost:" + port + "/api/auth/login");
            System.out.println("===============================================================");

        } catch (IOException e) {
            System.err.println(">> ERRO FATAL: Não foi possível iniciar o servidor HTTP na porta " + port);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static int getPort() {
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                return Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_PORT;
    }
}
