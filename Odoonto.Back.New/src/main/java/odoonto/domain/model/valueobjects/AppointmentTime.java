package odoonto.domain.model.valueobjects;

import odoonto.domain.exceptions.InvalidAppointmentTimeException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Objeto de valor que representa la hora y duración de una cita.
 * Es inmutable y se valida en el constructor.
 */
public final class AppointmentTime {
    private final LocalDateTime startTime;
    private final int durationMinutes;
    
    /**
     * Constantes para validación
     */
    private static final int MIN_DURATION_MINUTES = 30;
    private static final int MAX_DURATION_MINUTES = 180;
    private static final LocalTime EARLIEST_TIME = LocalTime.of(8, 0);
    private static final LocalTime LATEST_START_TIME = LocalTime.of(17, 0);
    
    /**
     * Constructor para crear un horario de cita válido.
     * 
     * @param startTime Fecha y hora de inicio de la cita
     * @param durationMinutes Duración en minutos
     * @throws InvalidAppointmentTimeException Si el horario no cumple con las reglas
     */
    public AppointmentTime(LocalDateTime startTime, int durationMinutes) {
        validateStartTime(startTime);
        validateDuration(durationMinutes);
        validateEndTimeInBusinessHours(startTime, durationMinutes);
        
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }
    
    /**
     * Valida que la hora de inicio sea válida
     * @param startTime Hora de inicio a validar
     * @throws InvalidAppointmentTimeException Si no es válida
     */
    private void validateStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new InvalidAppointmentTimeException("La hora de inicio no puede ser nula");
        }
        
        // No se puede agendar en el pasado
        LocalDateTime now = LocalDateTime.now();
        if (startTime.isBefore(now)) {
            throw new InvalidAppointmentTimeException("No se pueden agendar citas en el pasado");
        }
        
        // Debe iniciar en intervalos de 30 minutos (00 o 30)
        int minute = startTime.getMinute();
        if (minute != 0 && minute != 30) {
            throw new InvalidAppointmentTimeException(
                    "Las citas deben iniciar en intervalos de 30 minutos (:00 o :30)");
        }
        
        // Debe estar dentro del horario permitido
        LocalTime timeOfDay = startTime.toLocalTime();
        if (timeOfDay.isBefore(EARLIEST_TIME) || timeOfDay.isAfter(LATEST_START_TIME)) {
            throw new InvalidAppointmentTimeException(
                    "Las citas deben agendarse entre " + 
                    EARLIEST_TIME.format(DateTimeFormatter.ofPattern("HH:mm")) + 
                    " y " + 
                    LATEST_START_TIME.format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }
    
    /**
     * Valida que la duración sea válida
     * @param durationMinutes Duración a validar
     * @throws InvalidAppointmentTimeException Si no es válida
     */
    private void validateDuration(int durationMinutes) {
        if (durationMinutes < MIN_DURATION_MINUTES) {
            throw new InvalidAppointmentTimeException(
                    "La duración mínima de una cita es de " + MIN_DURATION_MINUTES + " minutos");
        }
        
        if (durationMinutes > MAX_DURATION_MINUTES) {
            throw new InvalidAppointmentTimeException(
                    "La duración máxima de una cita es de " + MAX_DURATION_MINUTES + " minutos");
        }
        
        // Debe ser en múltiplos de 30 minutos
        if (durationMinutes % 30 != 0) {
            throw new InvalidAppointmentTimeException(
                    "La duración debe ser en múltiplos de 30 minutos");
        }
    }
    
    /**
     * Valida que la hora de finalización esté dentro del horario
     * @param startTime Hora de inicio
     * @param durationMinutes Duración en minutos
     * @throws InvalidAppointmentTimeException Si no es válida
     */
    private void validateEndTimeInBusinessHours(LocalDateTime startTime, int durationMinutes) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        LocalTime endTimeOfDay = endTime.toLocalTime();
        
        // La cita no debe terminar después de las 18:00
        if (endTimeOfDay.isAfter(LocalTime.of(18, 0))) {
            throw new InvalidAppointmentTimeException(
                    "La cita no puede finalizar después de las 18:00");
        }
    }
    
    // Getters - No hay setters para mantener inmutabilidad
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    /**
     * Obtiene la hora de finalización de la cita
     * @return Fecha y hora de finalización
     */
    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }
    
    /**
     * Verifica si esta cita se solapa con otra
     * @param other Otra cita
     * @return true si hay solapamiento
     */
    public boolean overlapsWith(AppointmentTime other) {
        if (other == null) {
            return false;
        }
        
        LocalDateTime thisEnd = this.getEndTime();
        LocalDateTime otherEnd = other.getEndTime();
        
        // Hay solapamiento si:
        // 1. Esta cita comienza durante la otra
        // 2. Esta cita termina durante la otra
        // 3. Esta cita contiene completamente a la otra
        return (this.startTime.isBefore(otherEnd) && this.startTime.isAfter(other.startTime)) ||
               (thisEnd.isAfter(other.startTime) && thisEnd.isBefore(otherEnd)) ||
               (this.startTime.isBefore(other.startTime) && thisEnd.isAfter(otherEnd));
    }
    
    /**
     * Verifica si la cita es en un día específico
     * @param date Fecha a verificar
     * @return true si la cita es en esa fecha
     */
    public boolean isOnDate(java.time.LocalDate date) {
        return this.startTime.toLocalDate().equals(date);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AppointmentTime that = (AppointmentTime) o;
        return durationMinutes == that.durationMinutes &&
               Objects.equals(startTime, that.startTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(startTime, durationMinutes);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Inicio: " + startTime.format(formatter) + 
               ", Duración: " + durationMinutes + " minutos, " +
               "Fin: " + getEndTime().format(formatter);
    }
} 