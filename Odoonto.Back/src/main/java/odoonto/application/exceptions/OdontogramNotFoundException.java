package odoonto.application.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un odontograma
 */
public class OdontogramNotFoundException extends RuntimeException {
    
    /**
     * Construye una nueva excepción con el mensaje especificado
     * @param message El mensaje de error
     */
    public OdontogramNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Construye una nueva excepción con el mensaje y la causa especificados
     * @param message El mensaje de error
     * @param cause La causa de la excepción
     */
    public OdontogramNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 