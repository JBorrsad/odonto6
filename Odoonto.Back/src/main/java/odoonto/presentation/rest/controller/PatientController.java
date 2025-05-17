package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.port.in.patient.PatientCreateUseCase;
import odoonto.application.port.in.patient.PatientDeleteUseCase;
import odoonto.application.port.in.patient.PatientOdontogramUseCase;
import odoonto.application.port.in.patient.PatientQueryUseCase;
import odoonto.application.port.in.patient.PatientUpdateUseCase;

import java.util.List;

/**
 * Controlador REST para operaciones con pacientes
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    private final PatientQueryUseCase patientQueryUseCase;
    private final PatientCreateUseCase patientCreateUseCase;
    private final PatientUpdateUseCase patientUpdateUseCase;
    private final PatientDeleteUseCase patientDeleteUseCase;
    private final PatientOdontogramUseCase patientOdontogramUseCase;
    
    @Autowired
    public PatientController(
            PatientQueryUseCase patientQueryUseCase,
            PatientCreateUseCase patientCreateUseCase,
            PatientUpdateUseCase patientUpdateUseCase,
            PatientDeleteUseCase patientDeleteUseCase,
            PatientOdontogramUseCase patientOdontogramUseCase) {
        this.patientQueryUseCase = patientQueryUseCase;
        this.patientCreateUseCase = patientCreateUseCase;
        this.patientUpdateUseCase = patientUpdateUseCase;
        this.patientDeleteUseCase = patientDeleteUseCase;
        this.patientOdontogramUseCase = patientOdontogramUseCase;
    }
    
    /**
     * Obtiene todos los pacientes
     * @return Lista de DTOs de pacientes
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientQueryUseCase.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente
     * @return DTO del paciente o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        PatientDTO patient = patientQueryUseCase.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    
    /**
     * Crea un nuevo paciente
     * @param patientDTO DTO con datos del paciente
     * @return DTO del paciente creado
     */
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientCreateDTO patientDTO) {
        PatientDTO created = patientCreateUseCase.createPatient(patientDTO);
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
        PatientDTO updated = patientUpdateUseCase.updatePatient(id, patientDTO);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Elimina un paciente
     * @param id ID del paciente a eliminar
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientDeleteUseCase.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Busca pacientes por nombre o apellido
     * @param query Texto a buscar
     * @return Lista de DTOs de pacientes que coinciden con la búsqueda
     */
    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String query) {
        List<PatientDTO> patients = patientQueryUseCase.searchPatients(query);
        return ResponseEntity.ok(patients);
    }
    
    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return Odontograma del paciente
     */
    @GetMapping("/{patientId}/odontogram")
    public ResponseEntity<Object> getPatientOdontogram(@PathVariable String patientId) {
        return ResponseEntity.ok(patientOdontogramUseCase.getPatientOdontogram(patientId));
    }
    
    /**
     * Obtiene el historial médico de un paciente
     * @param patientId ID del paciente
     * @return Historial médico del paciente
     */
    @GetMapping("/{patientId}/medical-record")
    public ResponseEntity<Object> getPatientMedicalRecord(@PathVariable String patientId) {
        return ResponseEntity.ok(patientOdontogramUseCase.getPatientMedicalRecord(patientId));
    }
} 