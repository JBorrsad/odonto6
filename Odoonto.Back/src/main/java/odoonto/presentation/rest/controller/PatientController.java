package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.port.in.patient.PatientCreateUseCase;
import odoonto.application.port.in.patient.PatientDeleteUseCase;
import odoonto.application.port.in.patient.PatientOdontogramUseCase;
import odoonto.application.port.in.patient.PatientQueryUseCase;
import odoonto.application.port.in.patient.PatientUpdateUseCase;
import odoonto.application.service.PatientService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Controlador REST para operaciones con pacientes
 */
@RestController
public class PatientController {
    
    private final PatientQueryUseCase patientQueryUseCase;
    private final PatientCreateUseCase patientCreateUseCase;
    private final PatientUpdateUseCase patientUpdateUseCase;
    private final PatientDeleteUseCase patientDeleteUseCase;
    private final PatientOdontogramUseCase patientOdontogramUseCase;
    private final PatientService patientService;
    
    @Autowired
    public PatientController(
            PatientQueryUseCase patientQueryUseCase,
            PatientCreateUseCase patientCreateUseCase,
            PatientUpdateUseCase patientUpdateUseCase,
            PatientDeleteUseCase patientDeleteUseCase,
            PatientOdontogramUseCase patientOdontogramUseCase,
            PatientService patientService) {
        this.patientQueryUseCase = patientQueryUseCase;
        this.patientCreateUseCase = patientCreateUseCase;
        this.patientUpdateUseCase = patientUpdateUseCase;
        this.patientDeleteUseCase = patientDeleteUseCase;
        this.patientOdontogramUseCase = patientOdontogramUseCase;
        this.patientService = patientService;
    }
    
    // API sincrónica tradicional
    
    /**
     * Obtiene todos los pacientes
     * @return Lista de DTOs de pacientes
     */
    @GetMapping("/api/patients")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientQueryUseCase.getAllPatients();
        return ResponseEntity.ok(patients);
    }
    
    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente
     * @return DTO del paciente o 404 si no existe
     */
    @GetMapping("/api/patients/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String id) {
        PatientDTO patient = patientQueryUseCase.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
    
    /**
     * Crea un nuevo paciente
     * @param patientDTO DTO con datos del paciente
     * @return DTO del paciente creado
     */
    @PostMapping("/api/patients")
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
    @PutMapping("/api/patients/{id}")
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
    @DeleteMapping("/api/patients/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientDeleteUseCase.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Busca pacientes por nombre o apellido
     * @param query Texto a buscar
     * @return Lista de DTOs de pacientes que coinciden con la búsqueda
     */
    @GetMapping("/api/patients/search")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String query) {
        List<PatientDTO> patients = patientQueryUseCase.searchPatients(query);
        return ResponseEntity.ok(patients);
    }
    
    // API reactiva
    
    /**
     * Obtiene todos los pacientes de forma reactiva
     * @return Flux de DTOs de pacientes
     */
    @GetMapping(value = "/api/reactive/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<PatientDTO> getAllPatientsReactive() {
        return patientService.getAllPatientsReactive();
    }
    
    /**
     * Obtiene un paciente por su ID de forma reactiva
     * @param id ID del paciente
     * @return Mono con el DTO del paciente
     */
    @GetMapping(value = "/api/reactive/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PatientDTO> getPatientByIdReactive(@PathVariable String id) {
        return patientService.getPatientByIdReactive(id);
    }
    
    /**
     * Crea un nuevo paciente de forma reactiva
     * @param patientDTO DTO con datos del paciente
     * @return Mono con el DTO del paciente creado
     */
    @PostMapping(value = "/api/reactive/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PatientDTO> createPatientReactive(@RequestBody PatientCreateDTO patientDTO) {
        return patientService.createPatientReactive(patientDTO);
    }
    
    /**
     * Actualiza un paciente existente de forma reactiva
     * @param id ID del paciente a actualizar
     * @param patientDTO DTO con datos actualizados
     * @return Mono con el DTO del paciente actualizado
     */
    @PutMapping(value = "/api/reactive/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PatientDTO> updatePatientReactive(
            @PathVariable String id, 
            @RequestBody PatientCreateDTO patientDTO) {
        return patientService.updatePatientReactive(id, patientDTO);
    }
    
    /**
     * Elimina un paciente de forma reactiva
     * @param id ID del paciente a eliminar
     * @return Mono vacío que completa cuando se elimina el paciente
     */
    @DeleteMapping("/api/reactive/patients/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePatientReactive(@PathVariable String id) {
        return patientService.deletePatientReactive(id);
    }
    
    /**
     * Busca pacientes por nombre o apellido de forma reactiva
     * @param query Texto a buscar
     * @return Flux de DTOs de pacientes que coinciden con la búsqueda
     */
    @GetMapping(value = "/api/reactive/patients/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<PatientDTO> searchPatientsReactive(@RequestParam String query) {
        return patientService.findByNombreContainingOrApellidoContaining(query, query);
    }
} 