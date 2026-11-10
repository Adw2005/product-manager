package com.dmaxtech.productmanager.service;

import com.dmaxtech.productmanager.dao.UserDAO;
import com.dmaxtech.productmanager.dao.UserDAOImpl;
import com.dmaxtech.productmanager.dto.LoginRequest;
import com.dmaxtech.productmanager.dto.LoginResponse;
import com.dmaxtech.productmanager.exception.UnauthorizedException;
import com.dmaxtech.productmanager.exception.ValidationException;
import com.dmaxtech.productmanager.model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Base64;

/**
 * SERVICE responsável pela autenticação e emissão de tokens assinados com HMAC-SHA256 nativo do Java.
 */
public class AuthService {

    private static final String DEFAULT_SECRET = "dmaxtech-product-manager-super-secret-key-2026";
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24 horas

    private final UserDAO userDAO;
    private final String secretKey;

    public AuthService() {
        this(new UserDAOImpl());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
        String envSecret = System.getenv("JWT_SECRET");
        this.secretKey = (envSecret != null && !envSecret.isBlank()) ? envSecret.trim() : DEFAULT_SECRET;
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getUsername().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("Usuário e senha são obrigatórios.");
        }

        User user;
        try {
            user = userDAO.findByUsername(request.getUsername().trim())
                    .orElseThrow(() -> new UnauthorizedException("Usuário ou senha inválidos."));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar usuário no MySQL.", e);
        }

        boolean passwordMatch = false;
        try {
            passwordMatch = BCrypt.checkpw(request.getPassword(), user.getPassword());
        } catch (Exception e) {
            passwordMatch = false;
        }

        if (!passwordMatch) {
            throw new UnauthorizedException("Usuário ou senha inválidos.");
        }

        String token = generateToken(user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }

    /**
     * Gera um token assinado no formato:
     * Base64(username:expirationTimestamp).signature
     */
    public String generateToken(String username) {
        long expiresAt = System.currentTimeMillis() + EXPIRATION_MS;
        String payload = username + ":" + expiresAt;
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return encodedPayload + "." + signature;
    }

    /**
     * Valida o token e retorna o username se for válido e não expirado.
     */
    public String validateAndExtractUsername(String token) {
        if (token == null || !token.contains(".")) {
            return null;
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return null;
        }

        String payloadEncoded = parts[0];
        String signature = parts[1];

        // Verifica a integridade da assinatura
        String expectedSignature = sign(payloadEncoded);
        if (!MessageDigest.isEqual(signature.getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8))) {
            return null;
        }

        // Decodifica o payload
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(payloadEncoded);
            String payload = new String(bytes, StandardCharsets.UTF_8);
            String[] data = payload.split(":");
            if (data.length != 2) {
                return null;
            }

            String username = data[0];
            long expiresAt = Long.parseLong(data[1]);

            if (System.currentTimeMillis() > expiresAt) {
                return null; // Expirado
            }

            return username;
        } catch (Exception e) {
            return null;
        }
    }

    private String sign(String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);
            byte[] rawHmac = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao assinar token de autenticação.", e);
        }
    }
}

