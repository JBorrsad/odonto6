package odoonto.domain.exceptions;

/**
 * Excepción que se lanza cuando se intenta acceder a una cara de diente inválida
 */
public class InvalidToothFaceException extends DomainException {
    
    public InvalidToothFaceException(String face) {
        super("La cara de diente especificada no es válida: " + face);
    }
    
    public InvalidToothFaceException(String face, String toothId) {
        super("La cara '" + face + "' no es válida para el diente " + toothId);
    }
} 