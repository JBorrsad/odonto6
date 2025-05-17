package odoonto.application.exceptions;

/**
 * Excepción que se lanza cuando no se encuentra un doctor
 */
public class DoctorNotFoundException extends ApplicationException {
    
    public DoctorNotFoundException(String id) {
        super("No se encontró doctor con ID: " + id);
    }
} 