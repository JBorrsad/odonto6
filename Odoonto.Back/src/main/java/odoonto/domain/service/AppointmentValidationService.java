package odoonto.domain.service;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.exceptions.InvalidAppointmentTimeException;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.valueobjects.DoctorSchedule;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Servicio de dominio para validar citas según reglas de negocio
 */
public class AppointmentValidationService {
    
    // Duración mínima en minutos para una cita
    private static final int MIN_APPOINTMENT_DURATION = 30;
    
    // Duración máxima en minutos para una cita
    private static final int MAX_APPOINTMENT_DURATION = 180;
    
    // Horario normal de inicio de atención
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(8, 0);
    
    // Horario normal de fin de atención
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(18, 0);
    
    /**
     * Valida si la fecha y hora de la cita son válidas
     * @param dateTime Fecha y hora propuestas para la cita
     * @param duration Duración en minutos
     * @throws InvalidAppointmentTimeException Si la cita no cumple las reglas
     */
    public void validateAppointmentDateTime(LocalDateTime dateTime, int duration) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        
        // Validar que la fecha no sea en el pasado
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidAppointmentTimeException("No se pueden agendar citas en fechas pasadas");
        }
        
        // Validar que la hora sea en intervalos de 30 minutos (00 o 30)
        if (time.getMinute() != 0 && time.getMinute() != 30) {
            throw new InvalidAppointmentTimeException(
                    "Las citas deben iniciar en intervalos de 30 minutos (:00 o :30)");
        }
        
        // Validar duración
        if (duration < MIN_APPOINTMENT_DURATION) {
            throw new InvalidAppointmentTimeException(
                    "La duración mínima de una cita es de " + MIN_APPOINTMENT_DURATION + " minutos");
        }
        
        if (duration > MAX_APPOINTMENT_DURATION) {
            throw new InvalidAppointmentTimeException(
                    "La duración máxima de una cita es de " + MAX_APPOINTMENT_DURATION + " minutos");
        }
        
        // Validar que la duración sea en múltiplos de 30 minutos
        if (duration % 30 != 0) {
            throw new InvalidAppointmentTimeException(
                    "La duración debe ser en múltiplos de 30 minutos");
        }
    }
    
    /**
     * Valida si el doctor está disponible en el horario propuesto
     * @param doctor Doctor a consultar
     * @param dateTime Fecha y hora propuestas
     * @param duration Duración en minutos
     * @return true si el doctor está disponible
     */
    public boolean isDoctorAvailable(Doctor doctor, LocalDateTime dateTime, int duration) {
        // Usar la lógica implementada en Doctor para verificar disponibilidad
        return doctor.isAvailable(dateTime, duration);
    }
    
    /**
     * Verifica que no haya solapamiento con otras citas
     * @param existingAppointments Lista de citas existentes
     * @param proposedStart Inicio propuesto
     * @param duration Duración en minutos
     * @return true si no hay solapamiento
     */
    public boolean validateNoOverlap(List<Appointment> existingAppointments, 
                                    LocalDateTime proposedStart, 
                                    int duration) {
        LocalDateTime proposedEnd = proposedStart.plusMinutes(duration);
        
        for (Appointment existing : existingAppointments) {
            LocalDateTime existingStart = existing.getStartAsLocalDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationSlots() * 30);
            
            // Hay solapamiento si:
            // 1. La cita propuesta comienza durante una cita existente
            // 2. La cita propuesta termina durante una cita existente
            // 3. La cita propuesta contiene completamente una cita existente
            boolean overlaps = 
                (proposedStart.isBefore(existingEnd) && proposedStart.isAfter(existingStart)) ||
                (proposedEnd.isAfter(existingStart) && proposedEnd.isBefore(existingEnd)) ||
                (proposedStart.isBefore(existingStart) && proposedEnd.isAfter(existingEnd));
            
            if (overlaps) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calcula el próximo horario disponible para una cita
     * @param doctor Doctor para el que se busca disponibilidad
     * @param existingAppointments Citas existentes
     * @param fromDate Fecha desde la que buscar
     * @param duration Duración necesaria en minutos
     * @return Próximo horario disponible
     */
    public LocalDateTime findNextAvailableSlot(
            Doctor doctor, 
            List<Appointment> existingAppointments,
            LocalDate fromDate, 
            int duration) {
        
        LocalDate currentDate = fromDate;
        int maxDaysToSearch = 30; // Límite para evitar búsquedas infinitas
        
        for (int dayCount = 0; dayCount < maxDaysToSearch; dayCount++) {
            // Verificar si el doctor trabaja en esta fecha
            if (doctor.worksOnDate(currentDate)) {
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                DoctorSchedule schedule = doctor.getScheduleForDay(dayOfWeek);
                
                // Buscar horarios disponibles en este día
                LocalTime startOfDay = schedule.getStartTime();
                LocalTime endOfDay = schedule.getEndTime();
                
                // Empezar a buscar en intervalos de 30 minutos
                for (int hour = startOfDay.getHour(); hour <= endOfDay.getHour(); hour++) {
                    for (int minute = 0; minute < 60; minute += 30) {
                        // Saltamos si estamos fuera del horario del doctor
                        if (hour == endOfDay.getHour() && minute > 0) {
                            continue;
                        }
                        
                        LocalTime slotTime = LocalTime.of(hour, minute);
                        if (slotTime.isBefore(startOfDay) || slotTime.isAfter(endOfDay)) {
                            continue;
                        }
                        
                        LocalDateTime slotDateTime = LocalDateTime.of(currentDate, slotTime);
                        
                        // Verificar si este horario no está ocupado por otra cita
                        if (validateNoOverlap(existingAppointments, slotDateTime, duration)) {
                            return slotDateTime;
                        }
                    }
                }
            }
            
            // Pasar al siguiente día
            currentDate = currentDate.plusDays(1);
        }
        
        // Si no se encontró ningún horario disponible
        throw new DomainException("No se encontraron horarios disponibles en los próximos " + 
                                  maxDaysToSearch + " días");
    }
} 