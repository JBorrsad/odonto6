package odoonto.application.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un historial médico
 */
public class MedicalRecordNotFoundException extends RuntimeException {
    
    /**
     * Construye una nueva excepción con el mensaje especificado
     * @param message El mensaje de error
     */
    public MedicalRecordNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Construye una nueva excepción con el mensaje y la causa especificados
     * @param message El mensaje de error
     * @param cause La causa de la excepción
     */
    public MedicalRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 