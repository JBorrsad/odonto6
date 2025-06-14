package odoonto.domain.exceptions;

/**
 * Excepción que se lanza cuando un correo electrónico es inválido.
 */
public class InvalidEmailException extends DomainException {
    
    /**
     * Constructor con mensaje de error
     * @param message Mensaje descriptivo del error
     */
    public InvalidEmailException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor específico para email con formato inválido
     * @param email Email con formato inválido
     * @return InvalidEmailException con mensaje específico
     */
    public static InvalidEmailException forInvalidFormat(String email) {
        return new InvalidEmailException("El formato del email '" + email + "' es inválido. " +
                "Debe seguir el patrón usuario@dominio.extensión");
    }
    
    /**
     * Constructor específico para dominio inválido
     * @param domain Dominio inválido
     * @return InvalidEmailException con mensaje específico
     */
    public static InvalidEmailException forInvalidDomain(String domain) {
        return new InvalidEmailException("El dominio del email '" + domain + "' es inválido o no es accesible.");
    }
} 