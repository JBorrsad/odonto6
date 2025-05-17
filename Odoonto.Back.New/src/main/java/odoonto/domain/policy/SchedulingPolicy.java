package odoonto.domain.policy;

import odoonto.domain.exceptions.InvalidAppointmentTimeException;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.valueobjects.AppointmentStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Política que contiene reglas de negocio para la programación de citas.
 * Esta clase es inmutable y define reglas para la programación y modificación de citas.
 */
public class SchedulingPolicy {
    
    // Constantes de política (pueden volverse configurables en el futuro)
    private static final int MIN_DAYS_IN_ADVANCE = 1;
    private static final int MAX_DAYS_IN_ADVANCE = 60;
    private static final int MAX_DURATION_MINUTES = 180;
    private static final int MIN_DURATION_MINUTES = 15;
    private static final int DEFAULT_DURATION_MINUTES = 30;
    private static final LocalTime MORNING_START = LocalTime.of(8, 0);
    private static final LocalTime MORNING_END = LocalTime.of(12, 0);
    private static final LocalTime AFTERNOON_START = LocalTime.of(14, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(18, 0);
    
    /**
     * Valida si la fecha solicitada para una cita cumple con las reglas de política.
     * @param requestedDate Fecha solicitada
     * @throws InvalidAppointmentTimeException Si no cumple con las reglas
     */
    public void validateAppointmentDate(LocalDate requestedDate) {
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.plusDays(MIN_DAYS_IN_ADVANCE);
        LocalDate maxDate = today.plusDays(MAX_DAYS_IN_ADVANCE);
        
        if (requestedDate.isBefore(minDate)) {
            throw new InvalidAppointmentTimeException(
                    "Las citas deben programarse con al menos " + 
                    MIN_DAYS_IN_ADVANCE + " día(s) de anticipación");
        }
        
        if (requestedDate.isAfter(maxDate)) {
            throw new InvalidAppointmentTimeException(
                    "Las citas no pueden programarse con más de " + 
                    MAX_DAYS_IN_ADVANCE + " días de anticipación");
        }
        
        if (requestedDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
            requestedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new InvalidAppointmentTimeException(
                    "Las citas no pueden programarse en fines de semana");
        }
    }
    
    /**
     * Valida si la hora solicitada para una cita cumple con las reglas de política.
     * @param requestedTime Hora solicitada
     * @throws InvalidAppointmentTimeException Si no cumple con las reglas
     */
    public void validateAppointmentTime(LocalTime requestedTime) {
        // Verificar si está en los rangos permitidos
        boolean isMorningSlot = !requestedTime.isBefore(MORNING_START) && 
                              requestedTime.isBefore(MORNING_END);
        boolean isAfternoonSlot = !requestedTime.isBefore(AFTERNOON_START) && 
                                requestedTime.isBefore(AFTERNOON_END);
        
        if (!isMorningSlot && !isAfternoonSlot) {
            throw new InvalidAppointmentTimeException(
                    "Las citas solo pueden programarse en los horarios " +
                    MORNING_START + "-" + MORNING_END + " o " +
                    AFTERNOON_START + "-" + AFTERNOON_END);
        }
        
        // Verificar que comience en intervalos de 15 minutos
        int minutes = requestedTime.getMinute();
        if (minutes % 15 != 0) {
            throw new InvalidAppointmentTimeException(
                    "Las citas deben comenzar en intervalos de 15 minutos (00, 15, 30, 45)");
        }
    }
    
    /**
     * Valida si la duración de la cita cumple con las reglas de política.
     * @param durationMinutes Duración en minutos
     * @throws InvalidAppointmentTimeException Si no cumple con las reglas
     */
    public void validateAppointmentDuration(int durationMinutes) {
        if (durationMinutes < MIN_DURATION_MINUTES) {
            throw new InvalidAppointmentTimeException(
                    "La duración mínima de una cita es de " + 
                    MIN_DURATION_MINUTES + " minutos");
        }
        
        if (durationMinutes > MAX_DURATION_MINUTES) {
            throw new InvalidAppointmentTimeException(
                    "La duración máxima de una cita es de " + 
                    MAX_DURATION_MINUTES + " minutos");
        }
        
        // Duración debe ser en múltiplos de 15 minutos
        if (durationMinutes % 15 != 0) {
            throw new InvalidAppointmentTimeException(
                    "La duración debe ser en múltiplos de 15 minutos");
        }
    }
    
    /**
     * Verifica si hay solapamiento entre la cita propuesta y las existentes
     * @param existingAppointments Citas existentes
     * @param doctorId ID del doctor
     * @param proposedDateTime Fecha y hora propuestas
     * @param durationMinutes Duración propuesta
     * @return true si no hay solapamiento, false en caso contrario
     */
    public boolean checkOverlap(List<Appointment> existingAppointments, 
                               String doctorId,
                               LocalDateTime proposedDateTime, 
                               int durationMinutes) {
        LocalDateTime proposedEndTime = proposedDateTime.plusMinutes(durationMinutes);
        
        for (Appointment existing : existingAppointments) {
            // Ignorar citas canceladas
            if (existing.getStatus() == AppointmentStatus.CANCELLED) {
                continue;
            }
            
            // Verificar solo citas del mismo doctor
            if (!existing.getDoctorId().equals(doctorId)) {
                continue;
            }
            
            LocalDateTime existingStart = existing.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDuration());
            
            // Verificar si hay solapamiento
            if ((proposedDateTime.isBefore(existingEnd) && proposedEndTime.isAfter(existingStart))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Obtiene la duración predeterminada para citas según la política.
     * @return Duración en minutos
     */
    public int getDefaultDuration() {
        return DEFAULT_DURATION_MINUTES;
    }
    
    /**
     * Determina si un doctor puede atender citas en una fecha específica
     * @param doctor Doctor a verificar
     * @param date Fecha a verificar
     * @return true si el doctor puede atender en esa fecha
     */
    public boolean isDoctorAvailableOnDate(Doctor doctor, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        // Por defecto, todos los doctores atienden de lunes a viernes
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
        
        // Aquí se podría agregar lógica adicional para verificar días específicos
        // por doctor, vacaciones, licencias, etc.
    }
} 