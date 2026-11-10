package com.dmaxtech.productmanager.exception;

import java.util.Collections;
import java.util.Map;

/**
 * Exceção lançada quando os dados de entrada não atendem aos critérios de validação de negócio.
 */
public class ValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = Collections.emptyMap();
    }

    public ValidationException(Map<String, String> errors) {
        super("Erro de validação nos campos informados.");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}

