package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.service.PatientService;
import odoonto.application.exceptions.PatientNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones con pacientes
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    private final PatientService patientService;
    
    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    
    /**
     * Obtiene todos los pacientes
     * @return Lista de DTOs de pacientes
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente
     * @return DTO del paciente o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    
    /**
     * Crea un nuevo paciente
     * @param patientDTO DTO con datos del paciente
     * @return DTO del paciente creado
     */
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientCreateDTO patientDTO) {
        PatientDTO created = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Actualiza un paciente existente
     * @param id ID del paciente a actualizar
     * @param patientDTO DTO con datos actualizados
     * @return DTO del paciente actualizado o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable String id, 
            @RequestBody PatientCreateDTO patientDTO) {
        PatientDTO updated = patientService.updatePatient(id, patientDTO);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Elimina un paciente
     * @param id ID del paciente a eliminar
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Busca pacientes por nombre o apellido (filtrado en memoria)
     * @param query Texto a buscar
     * @return Lista de DTOs de pacientes que coinciden con la búsqueda
     */
    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String query) {
        // Como no hay un método específico, filtramos todos los pacientes
        String queryLower = query.toLowerCase();
        List<PatientDTO> patients = patientService.getAllPatients().stream()
            .filter(p -> p.getNombre().toLowerCase().contains(queryLower) || 
                     p.getApellido().toLowerCase().contains(queryLower))
            .collect(Collectors.toList());
        return ResponseEntity.ok(patients);
    }
    
    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return Odontograma del paciente
     */
    @GetMapping("/{patientId}/odontogram")
    public ResponseEntity<Object> getPatientOdontogram(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getPatientOdontogram(patientId));
    }
    
    /**
     * Obtiene el historial médico de un paciente
     * @param patientId ID del paciente
     * @return Historial médico del paciente
     */
    @GetMapping("/{patientId}/medical-record")
    public ResponseEntity<Object> getPatientMedicalRecord(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getPatientMedicalRecord(patientId));
    }
} 