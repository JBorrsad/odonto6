package odoonto.domain.exceptions;

/**
 * Excepción que se lanza cuando se intenta añadir una lesión duplicada
 */
public class DuplicateLesionException extends DomainException {
    
    public DuplicateLesionException(String toothId, String face) {
        super("Ya existe una lesión en la cara '" + face + "' del diente " + toothId);
    }
} 