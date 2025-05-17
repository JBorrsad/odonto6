package odoonto.domain.exceptions;

/**
 * Excepción que se lanza cuando un número telefónico es inválido.
 */
public class InvalidPhoneException extends DomainException {
    
    /**
     * Constructor con mensaje de error
     * @param message Mensaje descriptivo del error
     */
    public InvalidPhoneException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public InvalidPhoneException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor específico para teléfono con formato inválido
     * @param phone Teléfono con formato inválido
     * @return InvalidPhoneException con mensaje específico
     */
    public static InvalidPhoneException forInvalidFormat(String phone) {
        return new InvalidPhoneException("El formato del teléfono '" + phone + "' es inválido. " +
                "Debe contener solo dígitos, posible prefijo internacional (+) y guiones opcionales.");
    }
    
    /**
     * Constructor específico para longitud incorrecta
     * @param phone Teléfono con longitud incorrecta
     * @param expectedLength Longitud esperada
     * @return InvalidPhoneException con mensaje específico
     */
    public static InvalidPhoneException forInvalidLength(String phone, int expectedLength) {
        return new InvalidPhoneException("La longitud del teléfono '" + phone + "' es incorrecta. " +
                "Debe tener " + expectedLength + " dígitos.");
    }
} 