package com.dmaxtech.productmanager.controller;

import com.dmaxtech.productmanager.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Controller responsável por servir os arquivos estáticos do frontend puro (HTML, CSS e JavaScript).
 * Lê os arquivos do classpath (JAR) ou do diretório estático local.
 */
public class StaticFileController implements HttpHandler {

    private final String localStaticDir;

    public StaticFileController() {
        this.localStaticDir = "src/main/resources/static";
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.applyCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (path == null || path.equals("/") || path.isBlank()) {
            path = "/index.html";
        }

        // Remove prefixo para localizar no classpath
        String resourcePath = "static" + (path.startsWith("/") ? path : "/" + path);

        byte[] content = loadFile(resourcePath, path);
        if (content == null) {
            String errorMsg = "404 - Arquivo estático não encontrado: " + path;
            byte[] errBytes = errorMsg.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(404, errBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errBytes);
            }
            return;
        }

        String contentType = determineContentType(path);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private byte[] loadFile(String resourcePath, String path) {
        // 1. Tenta carregar do classpath (quando rodando como JAR empacotado)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                return is.readAllBytes();
            }
        } catch (IOException ignored) {
        }

        // 2. Tenta carregar a partir do disco local em desenvolvimento
        File file = new File(localStaticDir, path.startsWith("/") ? path.substring(1) : path);
        if (file.exists() && file.isFile()) {
            try (InputStream fis = new FileInputStream(file)) {
                return fis.readAllBytes();
            } catch (IOException ignored) {
            }
        }

        // 3. Fallback procurando na pasta frontend/
        File frontendFile = new File("../frontend", path.startsWith("/") ? path.substring(1) : path);
        if (frontendFile.exists() && frontendFile.isFile()) {
            try (InputStream fis = new FileInputStream(frontendFile)) {
                return fis.readAllBytes();
            } catch (IOException ignored) {
            }
        }

        return null;
    }

    private String determineContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".ico")) return "image/x-icon";
        if (path.endsWith(".json")) return "application/json; charset=UTF-8";
        return "application/octet-stream";
    }
}

