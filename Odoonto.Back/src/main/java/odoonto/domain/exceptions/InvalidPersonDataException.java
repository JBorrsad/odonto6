package odoonto.domain.exceptions;

/**
 * Excepción que se lanza cuando los datos personales son inválidos.
 */
public class InvalidPersonDataException extends DomainException {
    
    /**
     * Constructor con mensaje de error
     * @param message Mensaje descriptivo del error
     */
    public InvalidPersonDataException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public InvalidPersonDataException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor específico para nombre inválido
     * @param name Nombre inválido
     * @return InvalidPersonDataException con mensaje específico
     */
    public static InvalidPersonDataException forInvalidName(String name) {
        return new InvalidPersonDataException("El nombre '" + name + "' es inválido. " +
                "Debe contener solo letras, espacios y caracteres especiales permitidos.");
    }
    
    /**
     * Constructor específico para fecha de nacimiento inválida
     * @param reason Motivo de invalidez
     * @return InvalidPersonDataException con mensaje específico
     */
    public static InvalidPersonDataException forInvalidBirthDate(String reason) {
        return new InvalidPersonDataException("Fecha de nacimiento inválida: " + reason);
    }
} 