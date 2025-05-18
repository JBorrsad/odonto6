package odoonto.domain.specifications;

import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.valueobjects.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Especificaciones para consultas complejas sobre citas.
 */
public class AppointmentSpecification {
    
    /**
     * Crea una especificación para filtrar citas por doctor
     * @param doctorId ID del doctor
     * @return Especificación resultante
     */
    public static Specification<Appointment> byDoctor(String doctorId) {
        return appointment -> {
            if (doctorId == null || doctorId.isEmpty()) {
                return true;
            }
            
            return appointment.getDoctorId().equals(doctorId);
        };
    }
    
    /**
     * Crea una especificación para filtrar citas por paciente
     * @param patientId ID del paciente
     * @return Especificación resultante
     */
    public static Specification<Appointment> byPatient(String patientId) {
        return appointment -> {
            if (patientId == null || patientId.isEmpty()) {
                return true;
            }
            
            return appointment.getPatientId().equals(patientId);
        };
    }
    
    /**
     * Crea una especificación para filtrar citas por estado
     * @param status Estado de la cita
     * @return Especificación resultante
     */
    public static Specification<Appointment> byStatus(AppointmentStatus status) {
        return appointment -> {
            if (status == null) {
                return true;
            }
            
            return appointment.getStatus() == status;
        };
    }
    
    /**
     * Crea una especificación para filtrar citas por fecha
     * @param date Fecha a filtrar
     * @return Especificación resultante
     */
    public static Specification<Appointment> onDate(LocalDate date) {
        return appointment -> {
            if (date == null) {
                return true;
            }
            
            LocalDate appointmentDate = appointment.getDateTime().toLocalDate();
            return appointmentDate.equals(date);
        };
    }
    
    /**
     * Crea una especificación para filtrar citas entre un rango de fechas
     * @param startDate Fecha de inicio (inclusive)
     * @param endDate Fecha de fin (inclusive)
     * @return Especificación resultante
     */
    public static Specification<Appointment> betweenDates(LocalDate startDate, LocalDate endDate) {
        return appointment -> {
            if (startDate == null && endDate == null) {
                return true;
            }
            
            LocalDate appointmentDate = appointment.getDateTime().toLocalDate();
            
            if (startDate == null) {
                return !appointmentDate.isAfter(endDate);
            }
            
            if (endDate == null) {
                return !appointmentDate.isBefore(startDate);
            }
            
            return !appointmentDate.isBefore(startDate) && !appointmentDate.isAfter(endDate);
        };
    }
    
    /**
     * Crea una especificación para filtrar citas que ocurren durante un rango horario
     * @param startTime Hora de inicio (inclusive)
     * @param endTime Hora de fin (inclusive)
     * @return Especificación resultante
     */
    public static Specification<Appointment> duringTimeRange(LocalTime startTime, LocalTime endTime) {
        return appointment -> {
            if (startTime == null || endTime == null) {
                return true;
            }
            
            LocalTime appointmentTime = appointment.getDateTime().toLocalTime();
            return !appointmentTime.isBefore(startTime) && !appointmentTime.isAfter(endTime);
        };
    }
    
    /**
     * Crea una especificación para filtrar citas pendientes (próximas)
     * @return Especificación resultante
     */
    public static Specification<Appointment> isPending() {
        return appointment -> {
            LocalDateTime now = LocalDateTime.now();
            return appointment.getDateTime().isAfter(now) && 
                   appointment.getStatus() != AppointmentStatus.CANCELADA;
        };
    }
    
    /**
     * Crea una especificación para filtrar citas con duración mayor a un valor
     * @param minDuration Duración mínima en minutos
     * @return Especificación resultante
     */
    public static Specification<Appointment> withMinimumDuration(int minDuration) {
        return appointment -> appointment.getDurationSlots() >= minDuration;
    }
    
    /**
     * Crea una especificación para filtrar citas pendientes de confirmación
     * @return Especificación resultante
     */
    public static Specification<Appointment> needsConfirmation() {
        return appointment -> {
            return appointment.getStatus() == AppointmentStatus.PENDIENTE &&
                   appointment.getDateTime().isBefore(LocalDateTime.now().plusDays(2));
        };
    }
} 