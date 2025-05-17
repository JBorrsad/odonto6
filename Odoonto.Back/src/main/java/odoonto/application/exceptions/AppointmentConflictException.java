package odoonto.application.exceptions;

/**
 * Excepción que se lanza cuando hay un conflicto de citas (solapamiento)
 */
public class AppointmentConflictException extends ApplicationException {
    
    public AppointmentConflictException(String message) {
        super(message);
    }
    
    public AppointmentConflictException(String doctorId, String startTime) {
        super("Ya existe una cita para el doctor con ID: " + doctorId + 
              " en el horario: " + startTime);
    }
} 