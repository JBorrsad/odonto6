package odoonto.application.exceptions;

/**
 * Excepción que se lanza cuando no se encuentra un paciente
 */
public class PatientNotFoundException extends ApplicationException {
    
    public PatientNotFoundException(String id) {
        super("No se encontró paciente con ID: " + id);
    }
} 