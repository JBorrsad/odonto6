package odoonto.presentation.rest.controller;

import odoonto.application.dto.response.PatientDTO;
import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.service.PatientService;
import odoonto.application.exceptions.PatientNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(patientService.getAllPatients());
    }
    
    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente
     * @return DTO del paciente o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        try {
            PatientDTO patient = patientService.getPatientById(id);
            return ResponseEntity.ok(patient);
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Crea un nuevo paciente
     * @param createDTO DTO con datos del paciente
     * @return DTO del paciente creado
     */
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientCreateDTO createDTO) {
        PatientDTO created = patientService.createPatient(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Actualiza un paciente existente
     * @param id ID del paciente a actualizar
     * @param updateDTO DTO con datos actualizados
     * @return DTO del paciente actualizado o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable String id, 
            @RequestBody PatientCreateDTO updateDTO) {
        try {
            PatientDTO updated = patientService.updatePatient(id, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Elimina un paciente
     * @param id ID del paciente a eliminar
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Busca pacientes por nombre o apellido
     * @param query Texto a buscar
     * @return Lista de DTOs de pacientes que coinciden con la búsqueda
     */
    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String query) {
        List<PatientDTO> patients = patientService.searchPatientsByName(query);
        return ResponseEntity.ok(patients);
    }
} 