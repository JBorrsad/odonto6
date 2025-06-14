package odoonto.domain.exceptions;

import java.time.LocalDateTime;

/**
 * Excepción que se lanza cuando se intenta crear una cita con un horario inválido
 */
public class InvalidAppointmentTimeException extends DomainException {
    
    public InvalidAppointmentTimeException(String message) {
        super(message);
    }
    
    public InvalidAppointmentTimeException(LocalDateTime dateTime) {
        super("La hora de la cita es inválida: " + dateTime.toString() + 
              ". Las citas deben programarse en intervalos de 30 minutos (XX:00 o XX:30)");
    }
    
    public InvalidAppointmentTimeException(LocalDateTime start, int duration) {
        super("Combinación inválida de hora (" + start.toString() + 
              ") y duración (" + duration + " slots). Revise los parámetros.");
    }
} 