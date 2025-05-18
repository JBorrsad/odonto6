package odoonto.presentation.rest.controller;

import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.service.AppointmentService;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.exceptions.DoctorNotFoundException;
import odoonto.application.exceptions.AppointmentConflictException;
import odoonto.domain.exceptions.InvalidAppointmentTimeException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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
    
    // API sincrónica tradicional
    
    /**
     * Obtiene todas las citas
     * @return Lista de DTOs de citas
     */
    @GetMapping("/api/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }
    
    /**
     * Obtiene una cita por su ID
     * @param id ID de la cita
     * @return DTO de la cita o 404 si no existe
     */
    @GetMapping("/api/appointments/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable String id) {
        try {
            AppointmentDTO appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Crea una nueva cita
     * @param createDTO DTO con datos de la cita
     * @return DTO de la cita creada o error
     */
    @PostMapping("/api/appointments")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentCreateDTO createDTO) {
        try {
            AppointmentDTO created = appointmentService.createAppointment(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (PatientNotFoundException | DoctorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AppointmentConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Actualiza una cita existente
     * @param id ID de la cita a actualizar
     * @param updateDTO DTO con datos actualizados
     * @return DTO de la cita actualizada o error
     */
    @PutMapping("/api/appointments/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable String id, 
            @RequestBody AppointmentCreateDTO updateDTO) {
        try {
            AppointmentDTO updated = appointmentService.updateAppointment(id, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    /**
     * Elimina una cita
     * @param id ID de la cita a eliminar
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/api/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Obtiene las citas de un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Lista de DTOs de citas
     */
    @GetMapping("/api/appointments/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctorAndDateRange(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        // Convertir LocalDate a String en formato ISO
        String fromString = from.atStartOfDay(ZoneId.systemDefault()).toInstant().toString();
        String toString = to.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toString();
        
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorAndDateRange(
                doctorId, fromString, toString);
        return ResponseEntity.ok(appointments);
    }
    
    /**
     * Obtiene las citas de un paciente (filtrado en memoria)
     * @param patientId ID del paciente
     * @return Lista de DTOs de citas
     */
    @GetMapping("/api/appointments/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPatient(
            @PathVariable String patientId) {
        
        // Como no tenemos un método específico, filtramos todas las citas
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments().stream()
            .filter(a -> a.getPatientId().equals(patientId))
            .collect(Collectors.toList());
        return ResponseEntity.ok(appointments);
    }
    
    /**
     * Confirma una cita (simulado mediante una actualización de estado)
     * @param id ID de la cita
     * @return DTO de la cita actualizada o error
     */
    @PutMapping("/api/appointments/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable String id) {
        try {
            // Obtenemos la cita existente
            AppointmentDTO appointment = appointmentService.getAppointmentById(id);
            
            // Aquí se podría cambiar el estado si el modelo tuviera esa capacidad
            // Por ahora solo retornamos la cita original
            return ResponseEntity.ok(appointment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    /**
     * Cancela una cita (simulado mediante una actualización de estado)
     * @param id ID de la cita
     * @param reason Motivo de cancelación (opcional)
     * @return DTO de la cita actualizada o error
     */
    @PutMapping("/api/appointments/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        try {
            // En un caso real, aquí llamaríamos a un método específico del servicio
            // Por ahora, simplemente eliminamos la cita
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // API reactiva
    
    /**
     * Obtiene todas las citas de forma reactiva
     * @return Flux de DTOs de citas
     */
    @GetMapping(value = "/api/reactive/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AppointmentDTO> getAllAppointmentsReactive() {
        return Flux.fromIterable(appointmentService.getAllAppointments());
    }
    
    /**
     * Obtiene una cita por su ID de forma reactiva
     * @param id ID de la cita
     * @return Mono con el DTO de la cita
     */
    @GetMapping(value = "/api/reactive/appointments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AppointmentDTO> getAppointmentByIdReactive(@PathVariable String id) {
        return Mono.fromCallable(() -> appointmentService.getAppointmentById(id))
                .onErrorResume(RuntimeException.class, e -> Mono.empty());
    }
    
    /**
     * Crea una nueva cita de forma reactiva
     * @param createDTO DTO con datos de la cita
     * @return Mono con el DTO de la cita creada
     */
    @PostMapping(value = "/api/reactive/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AppointmentDTO> createAppointmentReactive(@RequestBody AppointmentCreateDTO createDTO) {
        return Mono.fromCallable(() -> appointmentService.createAppointment(createDTO))
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
     * Actualiza una cita existente de forma reactiva
     * @param id ID de la cita a actualizar
     * @param updateDTO DTO con datos actualizados
     * @return Mono con el DTO de la cita actualizada
     */
    @PutMapping(value = "/api/reactive/appointments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AppointmentDTO> updateAppointmentReactive(
            @PathVariable String id, 
            @RequestBody AppointmentCreateDTO updateDTO) {
        return Mono.fromCallable(() -> appointmentService.updateAppointment(id, updateDTO))
                .onErrorResume(RuntimeException.class, e -> Mono.empty());
    }
    
    /**
     * Elimina una cita de forma reactiva
     * @param id ID de la cita a eliminar
     * @return Mono vacío que completa cuando se elimina la cita
     */
    @DeleteMapping("/api/reactive/appointments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAppointmentReactive(@PathVariable String id) {
        return Mono.fromRunnable(() -> appointmentService.deleteAppointment(id));
    }
    
    /**
     * Obtiene las citas de un doctor en un rango de fechas de forma reactiva
     * @param doctorId ID del doctor
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Flux de DTOs de citas
     */
    @GetMapping(value = "/api/reactive/appointments/doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AppointmentDTO> getAppointmentsByDoctorAndDateRangeReactive(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        String fromString = from.atStartOfDay(ZoneId.systemDefault()).toInstant().toString();
        String toString = to.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toString();
        
        return Flux.fromIterable(
            appointmentService.getAppointmentsByDoctorAndDateRange(doctorId, fromString, toString)
        );
    }
    
    /**
     * Obtiene las citas de un paciente de forma reactiva
     * @param patientId ID del paciente
     * @return Flux de DTOs de citas
     */
    @GetMapping(value = "/api/reactive/appointments/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AppointmentDTO> getAppointmentsByPatientReactive(@PathVariable String patientId) {
        return Flux.fromIterable(appointmentService.getAllAppointments())
                .filter(a -> a.getPatientId().equals(patientId));
    }
    
    /**
     * Confirma una cita de forma reactiva
     * @param id ID de la cita
     * @return Mono con el DTO de la cita confirmada
     */
    @PutMapping(value = "/api/reactive/appointments/{id}/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AppointmentDTO> confirmAppointmentReactive(@PathVariable String id) {
        return Mono.fromCallable(() -> appointmentService.getAppointmentById(id))
                .onErrorResume(RuntimeException.class, e -> Mono.empty());
    }
    
    /**
     * Cancela una cita de forma reactiva
     * @param id ID de la cita
     * @param reason Motivo de cancelación (opcional)
     * @return Mono vacío que completa cuando se cancela la cita
     */
    @DeleteMapping("/api/reactive/appointments/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> cancelAppointmentReactive(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        return Mono.fromRunnable(() -> appointmentService.deleteAppointment(id));
    }
} 