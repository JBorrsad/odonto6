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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para operaciones con citas
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    /**
     * Obtiene todas las citas
     * @return Lista de DTOs de citas
     */
    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }
    
    /**
     * Obtiene una cita por su ID
     * @param id ID de la cita
     * @return DTO de la cita o 404 si no existe
     */
    @GetMapping("/{id}")
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
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentCreateDTO createDTO) {
        try {
            AppointmentDTO created = appointmentService.createAppointment(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (PatientNotFoundException | DoctorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AppointmentConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidAppointmentTimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Actualiza una cita existente
     * @param id ID de la cita a actualizar
     * @param updateDTO DTO con datos actualizados
     * @return DTO de la cita actualizada o error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable String id, 
            @RequestBody AppointmentCreateDTO updateDTO) {
        try {
            AppointmentDTO updated = appointmentService.updateAppointment(id, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidAppointmentTimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Elimina una cita
     * @param id ID de la cita a eliminar
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
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
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctorAndDateRange(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorAndDateRange(
                doctorId, from, to);
        return ResponseEntity.ok(appointments);
    }
    
    /**
     * Obtiene las citas de un paciente
     * @param patientId ID del paciente
     * @return Lista de DTOs de citas
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPatient(
            @PathVariable String patientId) {
        
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }
    
    /**
     * Confirma una cita
     * @param id ID de la cita
     * @return DTO de la cita actualizada o error
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable String id) {
        try {
            AppointmentDTO updated = appointmentService.confirmAppointment(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    /**
     * Cancela una cita
     * @param id ID de la cita
     * @param reason Motivo de cancelación (opcional)
     * @return DTO de la cita actualizada o error
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        try {
            AppointmentDTO updated = appointmentService.cancelAppointment(id, reason);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
} 