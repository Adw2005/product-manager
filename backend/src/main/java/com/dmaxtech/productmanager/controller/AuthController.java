package com.dmaxtech.productmanager.controller;

import com.dmaxtech.productmanager.dto.LoginRequest;
import com.dmaxtech.productmanager.dto.LoginResponse;
import com.dmaxtech.productmanager.exception.UnauthorizedException;
import com.dmaxtech.productmanager.exception.ValidationException;
import com.dmaxtech.productmanager.service.AuthService;
import com.dmaxtech.productmanager.util.HttpUtil;
import com.dmaxtech.productmanager.util.JsonUtil;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * CONTROLLER (Camada C do MVC)
 * Endpoint de autenticação do sistema (/api/auth/login).
 * Implementado com o HttpServer nativo do Java SE.
 */
public class AuthController implements HttpHandler {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtil.handlePreflight(exchange)) {
            return;
        }

        String method = exchange.getRequestMethod().toUpperCase();

        if ("POST".equals(method)) {
            handleLogin(exchange);
        } else {
            HttpUtil.sendError(exchange, 405, "Método Não Permitido", "Apenas requisições POST são suportadas neste endpoint.");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        try {
            String body = HttpUtil.readRequestBody(exchange);
            LoginRequest request = JsonUtil.fromJson(body, LoginRequest.class);

            LoginResponse response = authService.login(request);
            HttpUtil.sendJsonResponse(exchange, 200, response);
        } catch (ValidationException e) {
            HttpUtil.sendError(exchange, 400, "Dados Inválidos", e.getMessage(), e.getErrors());
        } catch (UnauthorizedException e) {
            HttpUtil.sendError(exchange, 401, "Não Autorizado", e.getMessage());
        } catch (JsonSyntaxException e) {
            HttpUtil.sendError(exchange, 400, "JSON Inválido", "O corpo da requisição não é um JSON válido.");
        } catch (Exception e) {
            HttpUtil.sendError(exchange, 500, "Erro Interno", e.getMessage());
        }
    }
}
