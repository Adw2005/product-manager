package com.dmaxtech.productmanager.exception;

/**
 * Exceção lançada quando a requisição não possui credenciais válidas ou token ausente/inválido.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

