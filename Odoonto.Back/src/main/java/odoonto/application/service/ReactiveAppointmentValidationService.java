package odoonto.application.service;

import odoonto.application.port.out.ReactiveAppointmentRepository;
import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.domain.exceptions.AppointmentOverlapException;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.exceptions.InvalidAppointmentTimeException;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.valueobjects.AppointmentTime;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Servicio reactivo para validación de citas.
 */
@Service
public class ReactiveAppointmentValidationService {
    
    private final ReactiveAppointmentRepository appointmentRepository;
    private final ReactiveDoctorRepository doctorRepository;
    
    // Constantes de negocio
    private static final int MIN_APPOINTMENT_DURATION = 30; // minutos
    private static final int MAX_APPOINTMENT_DURATION = 180; // minutos
    private static final LocalTime EARLIEST_TIME = LocalTime.of(8, 0); // 8:00 AM
    private static final LocalTime LATEST_START_TIME = LocalTime.of(17, 30); // 5:30 PM

    /**
     * Constructor
     */
    public ReactiveAppointmentValidationService(
            ReactiveAppointmentRepository appointmentRepository,
            ReactiveDoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }
    
    /**
     * Valida una nueva cita de forma reactiva
     * @param doctorId ID del doctor
     * @param dateTime Fecha y hora
     * @param durationMinutes Duración
     * @return Mono que completa si la validación es exitosa o emite error si falla
     */
    public Mono<Void> validateNewAppointment(String doctorId, LocalDateTime dateTime, int durationMinutes) {
        return validateAppointmentTime(dateTime, durationMinutes)
                .then(validateDoctorAvailability(doctorId, dateTime))
                .then(checkForOverlappingAppointments(doctorId, dateTime, durationMinutes, null));
    }
    
    /**
     * Valida la actualización de una cita de forma reactiva
     * @param appointmentId ID de la cita
     * @param doctorId ID del doctor
     * @param dateTime Nueva fecha y hora
     * @param durationMinutes Nueva duración
     * @return Mono que completa si la validación es exitosa o emite error si falla
     */
    public Mono<Void> validateAppointmentUpdate(
            String appointmentId, String doctorId, LocalDateTime dateTime, int durationMinutes) {
        return validateAppointmentTime(dateTime, durationMinutes)
                .then(validateDoctorAvailability(doctorId, dateTime))
                .then(checkForOverlappingAppointments(doctorId, dateTime, durationMinutes, appointmentId));
    }
    
    /**
     * Valida el formato y reglas básicas de tiempo para una cita
     */
    private Mono<Void> validateAppointmentTime(LocalDateTime dateTime, int durationMinutes) {
        return Mono.fromCallable(() -> {
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
            
            return true;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }
    
    /**
     * Valida que el doctor esté disponible en la fecha y hora especificada
     */
    private Mono<Void> validateDoctorAvailability(String doctorId, LocalDateTime dateTime) {
        return doctorRepository.findById(doctorId)
                .switchIfEmpty(Mono.error(new DomainException("Doctor no encontrado con ID: " + doctorId)))
                .flatMap(doctor -> {
                    if (!doctor.worksOnDate(dateTime.toLocalDate())) {
                        return Mono.error(new DomainException("El doctor no atiende en esa fecha: " + dateTime.toLocalDate()));
                    }
                    return Mono.empty();
                });
    }
    
    /**
     * Verifica si hay citas solapadas para el doctor en el horario especificado
     */
    private Mono<Void> checkForOverlappingAppointments(
            String doctorId, LocalDateTime dateTime, int durationMinutes, String excludeAppointmentId) {
        
        LocalDate date = dateTime.toLocalDate();
        
        return appointmentRepository.findByDoctorId(doctorId)
                .filter(appointment -> appointment.getDateTime().toLocalDate().equals(date))
                .filter(appointment -> excludeAppointmentId == null 
                        || !appointment.getId().equals(excludeAppointmentId))
                .collectList()
                .flatMap(appointments -> {
                    if (appointments.isEmpty()) {
                        return Mono.empty();
                    }
                    
                    // Crear objeto AppointmentTime para la nueva cita
                    AppointmentTime newAppointmentTime = new AppointmentTime(dateTime, durationMinutes);
                    
                    // Verificar solapamiento con cada cita existente
                    for (Appointment existingAppointment : appointments) {
                        // Crear objeto AppointmentTime para la cita existente
                        AppointmentTime existingTime = new AppointmentTime(
                                existingAppointment.getDateTime(), 
                                existingAppointment.getDurationSlots() * 30);
                        
                        // Verificar solapamiento
                        if (newAppointmentTime.overlaps(existingTime)) {
                            return Mono.error(new AppointmentOverlapException(
                                    existingAppointment.getId(),
                                    doctorId,
                                    existingAppointment.getDateTime().toString()));
                        }
                    }
                    
                    return Mono.empty();
                });
    }
} 