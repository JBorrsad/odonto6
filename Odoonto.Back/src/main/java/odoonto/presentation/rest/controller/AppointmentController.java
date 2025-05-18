package odoonto.presentation.rest.controller;

import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.service.AppointmentService;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.exceptions.DoctorNotFoundException;
import odoonto.application.exceptions.AppointmentConflictException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Controlador REST para operaciones con citas
 */
@RestController
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    /**
     * Obtiene todas las citas
     * @return Flux de DTOs de citas
     */
    @GetMapping(value = "/api/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }
    
    /**
     * Obtiene una cita por su ID
     * @param id ID de la cita
     * @return Mono con el DTO de la cita
     */
    @GetMapping(value = "/api/appointments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AppointmentDTO> getAppointmentById(@PathVariable String id) {
        return appointmentService.getAppointmentById(id)
                .onErrorResume(RuntimeException.class, e -> Mono.empty());
    }
    
    /**
     * Crea una nueva cita
     * @param createDTO DTO con datos de la cita
     * @return Mono con el DTO de la cita creada
     */
    @PostMapping(value = "/api/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AppointmentDTO> createAppointment(@RequestBody AppointmentCreateDTO createDTO) {
        return appointmentService.createAppointment(createDTO)
                .onErrorResume(e -> {
                    if (e instanceof PatientNotFoundException || e instanceof DoctorNotFoundException) {
                        return Mono.error(e);
                    } else if (e instanceof AppointmentConflictException) {
                        return Mono.error(e);
                    } else {
                        return Mono.error(new RuntimeException("Error creando cita: " + e.getMessage()));
                    }
                });
    }
    
    /**
     * Actualiza una cita existente
     * @param id ID de la cita a actualizar
     * @param updateDTO DTO con datos actualizados
     * @return Mono con el DTO de la cita actualizada
     */
    @PutMapping(value = "/api/appointments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AppointmentDTO> updateAppointment(
            @PathVariable String id, 
            @RequestBody AppointmentCreateDTO updateDTO) {
        return appointmentService.updateAppointment(id, updateDTO)
                .onErrorResume(RuntimeException.class, e -> Mono.empty());
    }
    
    /**
     * Elimina una cita
     * @param id ID de la cita a eliminar
     * @return Mono vacío que completa cuando se elimina la cita
     */
    @DeleteMapping("/api/appointments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAppointment(@PathVariable String id) {
        return Mono.fromRunnable(() -> appointmentService.deleteAppointment(id));
    }
    
    /**
     * Obtiene las citas de un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Flux de DTOs de citas
     */
    @GetMapping(value = "/api/appointments/doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AppointmentDTO> getAppointmentsByDoctorAndDateRange(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        String fromString = from.atStartOfDay(ZoneId.systemDefault()).toInstant().toString();
        String toString = to.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toString();
        
        return appointmentService.getAppointmentsByDoctorAndDateRange(doctorId, fromString, toString);
    }
    
    /**
     * Obtiene las citas de un paciente
     * @param patientId ID del paciente
     * @return Flux de DTOs de citas
     */
    @GetMapping(value = "/api/appointments/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AppointmentDTO> getAppointmentsByPatient(@PathVariable String patientId) {
        return appointmentService.getAllAppointments()
                .filter(a -> a.getPatientId().equals(patientId));
    }
    
    /**
     * Confirma una cita
     * @param id ID de la cita
     * @return Mono con el DTO de la cita confirmada
     */
    @PutMapping(value = "/api/appointments/{id}/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AppointmentDTO> confirmAppointment(@PathVariable String id) {
        return appointmentService.getAppointmentById(id)
                .onErrorResume(RuntimeException.class, e -> Mono.empty());
    }
    
    /**
     * Cancela una cita
     * @param id ID de la cita
     * @param reason Motivo de cancelación (opcional)
     * @return Mono vacío que completa cuando se cancela la cita
     */
    @DeleteMapping("/api/appointments/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> cancelAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        return Mono.fromRunnable(() -> appointmentService.deleteAppointment(id));
    }
} 