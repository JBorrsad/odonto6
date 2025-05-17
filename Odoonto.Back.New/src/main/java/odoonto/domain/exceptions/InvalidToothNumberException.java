package odoonto.domain.exceptions;

/**
 * Excepción que se lanza cuando un número de diente es inválido.
 */
public class InvalidToothNumberException extends DomainException {
    
    /**
     * Constructor con mensaje de error
     * @param message Mensaje descriptivo del error
     */
    public InvalidToothNumberException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public InvalidToothNumberException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor específico para número de diente fuera de rango
     * @param toothNumber Número de diente inválido
     * @return InvalidToothNumberException con mensaje específico
     */
    public static InvalidToothNumberException forOutOfRange(int toothNumber) {
        return new InvalidToothNumberException("El número de diente '" + toothNumber + "' está fuera del rango válido (1-32 o 51-85).");
    }
    
    /**
     * Constructor específico para notación inválida
     * @param notation Notación inválida
     * @return InvalidToothNumberException con mensaje específico
     */
    public static InvalidToothNumberException forInvalidNotation(String notation) {
        return new InvalidToothNumberException("La notación '" + notation + "' es inválida. " +
                "Debe seguir el sistema de numeración universal (1-32) o FDI (11-48).");
    }
} 