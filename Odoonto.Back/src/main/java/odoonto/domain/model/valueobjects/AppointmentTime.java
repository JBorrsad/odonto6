package odoonto.domain.model.valueobjects;

import odoonto.domain.exceptions.InvalidAppointmentTimeException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Objeto de valor que representa el horario de una cita dental.
 * Incluye la hora de inicio y duración. Es inmutable.
 */
public final class AppointmentTime {
    private final LocalDateTime startDateTime;
    private final int durationMinutes;
    
    /**
     * Constructor para crear un horario de cita válido
     * @param startDateTime Fecha y hora de inicio de la cita
     * @param durationMinutes Duración en minutos (debe ser múltiplo de 30)
     * @throws InvalidAppointmentTimeException si los parámetros son inválidos
     */
    public AppointmentTime(LocalDateTime startDateTime, int durationMinutes) {
        validateStartDateTime(startDateTime);
        validateDuration(durationMinutes);
        
        this.startDateTime = startDateTime;
        this.durationMinutes = durationMinutes;
    }
    
    /**
     * Valida que la fecha y hora de inicio sea válida
     * @param dateTime Fecha y hora a validar
     * @throws InvalidAppointmentTimeException si la fecha/hora es inválida
     */
    private void validateStartDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new InvalidAppointmentTimeException("La fecha y hora de inicio no puede ser nula");
        }
        
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeException("La fecha de la cita no puede ser en el pasado");
        }
        
        // Validar que la hora empiece en intervalos de 30 minutos (XX:00 o XX:30)
        int minute = dateTime.getMinute();
        if (minute != 0 && minute != 30) {
            throw new InvalidAppointmentTimeException("La hora debe comenzar en :00 o :30");
        }
    }
    
    /**
     * Valida que la duración sea válida
     * @param minutes Duración en minutos
     * @throws InvalidAppointmentTimeException si la duración es inválida
     */
    private void validateDuration(int minutes) {
        if (minutes <= 0) {
            throw new InvalidAppointmentTimeException("La duración debe ser mayor a 0 minutos");
        }
        
        if (minutes % 30 != 0) {
            throw new InvalidAppointmentTimeException("La duración debe ser en bloques de 30 minutos");
        }
        
        if (minutes > 180) {
            throw new InvalidAppointmentTimeException("La duración máxima de una cita es 180 minutos (3 horas)");
        }
    }
    
    /**
     * @return Fecha y hora de inicio de la cita
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    
    /**
     * @return Duración en minutos
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    /**
     * @return Fecha y hora de finalización de la cita
     */
    public LocalDateTime getEndDateTime() {
        return startDateTime.plusMinutes(durationMinutes);
    }
    
    /**
     * Verifica si este horario se superpone con otro
     * @param other Otro horario de cita
     * @return true si hay superposición
     */
    public boolean overlaps(AppointmentTime other) {
        if (other == null) {
            return false;
        }
        
        LocalDateTime thisEnd = this.getEndDateTime();
        LocalDateTime otherStart = other.getStartDateTime();
        LocalDateTime otherEnd = other.getEndDateTime();
        
        // No hay superposición si este termina antes de que el otro comience
        // o si este comienza después de que el otro termina
        return !(thisEnd.isBefore(otherStart) || this.startDateTime.isAfter(otherEnd));
    }
    
    /**
     * Verifica si este horario está dentro del horario laboral estándar
     * @return true si el horario está dentro del horario laboral (8am-7pm)
     */
    public boolean isWithinBusinessHours() {
        LocalTime start = startDateTime.toLocalTime();
        LocalTime end = getEndDateTime().toLocalTime();
        
        LocalTime businessStart = LocalTime.of(8, 0);
        LocalTime businessEnd = LocalTime.of(19, 0);
        
        return !start.isBefore(businessStart) && !end.isAfter(businessEnd);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AppointmentTime that = (AppointmentTime) o;
        return durationMinutes == that.durationMinutes &&
               Objects.equals(startDateTime, that.startDateTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(startDateTime, durationMinutes);
    }
    
    @Override
    public String toString() {
        return startDateTime.toLocalDate() + " " + 
               startDateTime.toLocalTime() + " - " + 
               getEndDateTime().toLocalTime() + 
               " (" + durationMinutes + " min)";
    }
} 