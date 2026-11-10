package com.dmaxtech.productmanager.controller;

import com.dmaxtech.productmanager.exception.ResourceNotFoundException;
import com.dmaxtech.productmanager.exception.ValidationException;
import com.dmaxtech.productmanager.model.Product;
import com.dmaxtech.productmanager.service.AuthService;
import com.dmaxtech.productmanager.service.ProductService;
import com.dmaxtech.productmanager.util.HttpUtil;
import com.dmaxtech.productmanager.util.JsonUtil;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * CONTROLLER (Camada C do MVC)
 * Gerencia as requisições HTTP RESTful da rota /api/products.
 * Implementa o CRUD completo com verbos HTTP semânticos (GET, POST, PUT, DELETE).
 * Utiliza o HttpServer nativo do Java SE sem frameworks.
 */
public class ProductController implements HttpHandler {

    private final ProductService productService;
    private final AuthService authService;

    public ProductController(ProductService productService, AuthService authService) {
        this.productService = productService;
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtil.handlePreflight(exchange)) {
            return;
        }

        // 1. Verificação de Autenticação (Bearer Token)
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            HttpUtil.sendError(exchange, 401, "Não Autorizado", "Token de autenticação ausente ou inválido.");
            return;
        }

        String token = authHeader.substring(7).trim();
        String authenticatedUser = authService.validateAndExtractUsername(token);
        if (authenticatedUser == null) {
            HttpUtil.sendError(exchange, 401, "Não Autorizado", "Sessão expirada ou token inválido.");
            return;
        }

        // 2. Extração do ID e Parâmetros da Rota
        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath(); // Ex: /api/products ou /api/products/1
        String query = requestUri.getQuery();
        String method = exchange.getRequestMethod().toUpperCase();

        Long id = extractIdFromPath(path);

        try {
            switch (method) {
                case "GET":
                    if (id == null) {
                        handleList(exchange, query);
                    } else {
                        handleGetById(exchange, id);
                    }
                    break;

                case "POST":
                    handleCreate(exchange);
                    break;

                case "PUT":
                    if (id == null) {
                        HttpUtil.sendError(exchange, 400, "Requisição Inválida", "Informe o ID do produto na URL para atualização.");
                    } else {
                        handleUpdate(exchange, id);
                    }
                    break;

                case "DELETE":
                    if (id == null) {
                        HttpUtil.sendError(exchange, 400, "Requisição Inválida", "Informe o ID do produto na URL para exclusão.");
                    } else {
                        handleDelete(exchange, id);
                    }
                    break;

                default:
                    HttpUtil.sendError(exchange, 405, "Método Não Permitido", "Método HTTP " + method + " não suportado.");
                    break;
            }
        } catch (ValidationException e) {
            HttpUtil.sendError(exchange, 400, "Dados Inválidos", e.getMessage(), e.getErrors());
        } catch (ResourceNotFoundException e) {
            HttpUtil.sendError(exchange, 404, "Não Encontrado", e.getMessage());
        } catch (JsonSyntaxException e) {
            HttpUtil.sendError(exchange, 400, "JSON Inválido", "O corpo da requisição não é um JSON válido.");
        } catch (Exception e) {
            HttpUtil.sendError(exchange, 500, "Erro Interno do Servidor", e.getMessage() != null ? e.getMessage() : "Erro inesperado.");
        }
    }

    private void handleList(HttpExchange exchange, String query) throws IOException {
        String search = extractQueryParam(query, "search");
        List<Product> products;
        if (search != null && !search.isBlank()) {
            products = productService.search(search);
        } else {
            products = productService.findAll();
        }
        HttpUtil.sendJsonResponse(exchange, 200, products);
    }

    private void handleGetById(HttpExchange exchange, Long id) throws IOException {
        Product product = productService.findById(id);
        HttpUtil.sendJsonResponse(exchange, 200, product);
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        String body = HttpUtil.readRequestBody(exchange);
        Product product = JsonUtil.fromJson(body, Product.class);
        Product saved = productService.create(product);
        HttpUtil.sendJsonResponse(exchange, 201, saved);
    }

    private void handleUpdate(HttpExchange exchange, Long id) throws IOException {
        String body = HttpUtil.readRequestBody(exchange);
        Product product = JsonUtil.fromJson(body, Product.class);
        Product updated = productService.update(id, product);
        HttpUtil.sendJsonResponse(exchange, 200, updated);
    }

    private void handleDelete(HttpExchange exchange, Long id) throws IOException {
        productService.delete(id);
        HttpUtil.sendNoContent(exchange);
    }

    private Long extractIdFromPath(String path) {
        // Aceita /api/products/123 ou /api/products/123/
        String prefix = "/api/products";
        if (path.startsWith(prefix)) {
            String remainder = path.substring(prefix.length());
            if (remainder.startsWith("/")) {
                remainder = remainder.substring(1);
            }
            if (!remainder.isBlank()) {
                int nextSlash = remainder.indexOf('/');
                String idStr = (nextSlash != -1) ? remainder.substring(0, nextSlash) : remainder;
                try {
                    return Long.parseLong(idStr);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return null;
    }

    private String extractQueryParam(String query, String paramName) {
        if (query == null || query.isBlank()) {
            return null;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length > 0 && kv[0].equalsIgnoreCase(paramName)) {
                return (kv.length > 1) ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            }
        }
        return null;
    }
}
