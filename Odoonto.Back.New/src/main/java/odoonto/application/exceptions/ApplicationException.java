package odoonto.application.exceptions;

/**
 * Excepción base para todas las excepciones de aplicación
 */
public class ApplicationException extends RuntimeException {
    
    public ApplicationException(String message) {
        super(message);
    }
    
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
} 