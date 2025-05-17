package odoonto.application.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un paciente
 */
public class PatientNotFoundException extends RuntimeException {
    
    public PatientNotFoundException(String patientId) {
        super("No se encontró el paciente con ID: " + patientId);
    }
    
    public PatientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 