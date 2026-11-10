package com.dmaxtech.productmanager.util;

import com.dmaxtech.productmanager.dto.ErrorResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Utilitário para facilitar a leitura e envio de respostas HTTP RESTful com o HttpServer nativo do Java.
 */
public class HttpUtil {

    public static void applyCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, X-Requested-With");
        headers.set("Access-Control-Max-Age", "3600");
    }

    public static boolean handlePreflight(HttpExchange exchange) throws IOException {
        applyCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return true;
        }
        return false;
    }

    public static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    public static void sendJsonResponse(HttpExchange exchange, int statusCode, Object body) throws IOException {
        applyCorsHeaders(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        String json = JsonUtil.toJson(body);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendNoContent(HttpExchange exchange) throws IOException {
        applyCorsHeaders(exchange);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    public static void sendError(HttpExchange exchange, int statusCode, String error, String message) throws IOException {
        sendError(exchange, statusCode, error, message, null);
    }

    public static void sendError(HttpExchange exchange, int statusCode, String error, String message, Map<String, String> errors) throws IOException {
        ErrorResponse err = new ErrorResponse(statusCode, error, message, errors);
        sendJsonResponse(exchange, statusCode, err);
    }
}

