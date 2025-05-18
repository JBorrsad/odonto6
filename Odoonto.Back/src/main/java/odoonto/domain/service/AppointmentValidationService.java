package odoonto.domain.service;

import odoonto.domain.exceptions.AppointmentOverlapException;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.exceptions.InvalidAppointmentTimeException;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.valueobjects.AppointmentTime;
import odoonto.domain.repository.AppointmentRepository;
import odoonto.domain.repository.DoctorRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Servicio de dominio para validar citas médicas.
 * Este servicio implementa lógica de negocio relacionada con la programación de citas
 * que no pertenece directamente a ninguna entidad o agregado.
 */
public class AppointmentValidationService {
    
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    
    // Constantes de negocio
    private static final int MIN_APPOINTMENT_DURATION = 30; // minutos
    private static final int MAX_APPOINTMENT_DURATION = 180; // minutos
    private static final LocalTime EARLIEST_TIME = LocalTime.of(8, 0); // 8:00 AM
    private static final LocalTime LATEST_START_TIME = LocalTime.of(17, 30); // 5:30 PM

    /**
     * Constructor
     * @param appointmentRepository Repositorio de citas
     * @param doctorRepository Repositorio de doctores
     */
    public AppointmentValidationService(AppointmentRepository appointmentRepository,
                                      DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }
    
    /**
     * Valida una nueva cita para asegurar que cumple todas las reglas de negocio
     * @param doctorId ID del doctor
     * @param dateTime Fecha y hora de la cita
     * @param durationMinutes Duración en minutos
     * @throws DomainException si la cita no cumple con las reglas de negocio
     */
    public void validateNewAppointment(String doctorId, LocalDateTime dateTime, int durationMinutes) {
        // Validar formato y reglas básicas de tiempo
        validateAppointmentTime(dateTime, durationMinutes);
        
        // Validar disponibilidad del doctor
        validateDoctorAvailability(doctorId, dateTime);
        
        // Validar que no haya solapamiento con otras citas
        checkForOverlappingAppointments(doctorId, dateTime, durationMinutes, null);
    }
    
    /**
     * Valida la actualización de una cita existente
     * @param appointmentId ID de la cita que se está actualizando
     * @param doctorId ID del doctor
     * @param dateTime Nueva fecha y hora
     * @param durationMinutes Nueva duración
     * @throws DomainException si la cita actualizada no cumple con las reglas de negocio
     */
    public void validateAppointmentUpdate(String appointmentId, String doctorId, 
                                        LocalDateTime dateTime, int durationMinutes) {
        // Validar formato y reglas básicas de tiempo
        validateAppointmentTime(dateTime, durationMinutes);
        
        // Validar disponibilidad del doctor
        validateDoctorAvailability(doctorId, dateTime);
        
        // Validar que no haya solapamiento con otras citas (excluyendo la propia cita)
        checkForOverlappingAppointments(doctorId, dateTime, durationMinutes, appointmentId);
    }
    
    /**
     * Valida el formato y reglas básicas de tiempo para una cita
     * @param dateTime Fecha y hora de la cita
     * @param durationMinutes Duración en minutos
     * @throws InvalidAppointmentTimeException si el tiempo no cumple con las reglas
     */
    private void validateAppointmentTime(LocalDateTime dateTime, int durationMinutes) {
        if (dateTime == null) {
            throw new InvalidAppointmentTimeException("La fecha y hora no pueden ser nulas");
        }
        
        // Validar que la cita no sea en el pasado
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeException("No se pueden agendar citas en el pasado");
        }
        
        // Validar que la cita no sea en domingo
        if (dateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new InvalidAppointmentTimeException("No se atienden citas los domingos");
        }
        
        // Validar que la cita empiece a horas o medias horas (XX:00 o XX:30)
        int minute = dateTime.getMinute();
        if (minute != 0 && minute != 30) {
            throw new InvalidAppointmentTimeException(
                    "Las citas deben comenzar en intervalos de 30 minutos (:00 o :30)");
        }
        
        // Validar el horario de atención
        LocalTime time = dateTime.toLocalTime();
        if (time.isBefore(EARLIEST_TIME) || time.isAfter(LATEST_START_TIME)) {
            throw new InvalidAppointmentTimeException(
                    "El horario de atención es de " + EARLIEST_TIME + " a " + LATEST_START_TIME);
        }
        
        // Validar la duración
        if (durationMinutes < MIN_APPOINTMENT_DURATION) {
            throw new InvalidAppointmentTimeException(
                    "La duración mínima de una cita es de " + MIN_APPOINTMENT_DURATION + " minutos");
        }
        
        if (durationMinutes > MAX_APPOINTMENT_DURATION) {
            throw new InvalidAppointmentTimeException(
                    "La duración máxima de una cita es de " + MAX_APPOINTMENT_DURATION + " minutos");
        }
        
        if (durationMinutes % 30 != 0) {
            throw new InvalidAppointmentTimeException(
                    "La duración debe ser en múltiplos de 30 minutos");
        }
        
        // Validar que la cita no termine después del horario de atención
        LocalTime endTime = time.plusMinutes(durationMinutes);
        if (endTime.isAfter(LocalTime.of(18, 0))) {
            throw new InvalidAppointmentTimeException(
                    "La cita no puede terminar después de las 18:00");
        }
    }
    
    /**
     * Valida que el doctor esté disponible en la fecha y hora especificada
     * @param doctorId ID del doctor
     * @param dateTime Fecha y hora de la cita
     * @throws DomainException si el doctor no está disponible
     */
    private void validateDoctorAvailability(String doctorId, LocalDateTime dateTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DomainException("Doctor no encontrado con ID: " + doctorId));
        
        // Verificar si el doctor trabaja en esa fecha
        if (!doctor.worksOnDate(dateTime.toLocalDate())) {
            throw new DomainException("El doctor no atiende en esa fecha: " + dateTime.toLocalDate());
        }
    }
    
    /**
     * Verifica si hay citas solapadas para el doctor en el horario especificado
     * @param doctorId ID del doctor
     * @param dateTime Fecha y hora de la cita
     * @param durationMinutes Duración en minutos
     * @param excludeAppointmentId ID de cita a excluir de la verificación (útil para actualizaciones)
     * @throws AppointmentOverlapException si hay solapamiento con otra cita
     */
    private void checkForOverlappingAppointments(String doctorId, LocalDateTime dateTime, 
                                             int durationMinutes, String excludeAppointmentId) {
        LocalDate date = dateTime.toLocalDate();
        
        // Obtener todas las citas del doctor para esa fecha
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorId(doctorId);
        
        // Filtrar las citas para la fecha específica
        List<Appointment> appointmentsForDate = doctorAppointments.stream()
                .filter(appointment -> appointment.getDateTime().toLocalDate().equals(date))
                .toList();
        
        if (appointmentsForDate.isEmpty()) {
            return; // No hay citas para verificar
        }
        
        // Crear objeto AppointmentTime para la nueva cita
        AppointmentTime newAppointmentTime = new AppointmentTime(dateTime, durationMinutes);
        
        // Verificar solapamiento con cada cita existente
        for (Appointment existingAppointment : appointmentsForDate) {
            // Si estamos actualizando una cita, excluirla de la verificación
            if (excludeAppointmentId != null && existingAppointment.getId().equals(excludeAppointmentId)) {
                continue;
            }
            
            // Crear objeto AppointmentTime para la cita existente
            AppointmentTime existingTime = new AppointmentTime(
                    existingAppointment.getDateTime(), 
                    existingAppointment.getDurationSlots() * 30);
            
            // Verificar solapamiento
            if (newAppointmentTime.overlaps(existingTime)) {
                throw new AppointmentOverlapException(
                        existingAppointment.getId(),
                        doctorId,
                        existingAppointment.getDateTime().toString());
            }
        }
    }
} 