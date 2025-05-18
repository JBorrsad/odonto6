package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.port.in.patient.PatientCreateUseCase;
import odoonto.application.port.in.patient.PatientDeleteUseCase;
import odoonto.application.port.in.patient.PatientOdontogramUseCase;
import odoonto.application.port.in.patient.PatientQueryUseCase;
import odoonto.application.port.in.patient.PatientUpdateUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
     * @return Flux de DTOs de pacientes
     */
    @GetMapping(value = "/api/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<PatientDTO> getAllPatients() {
        return patientQueryUseCase.getAllPatients();
    }
    
    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente
     * @return Mono con el DTO del paciente
     */
    @GetMapping(value = "/api/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PatientDTO> getPatientById(@PathVariable String id) {
        return patientQueryUseCase.getPatientById(id);
    }
    
    /**
     * Crea un nuevo paciente
     * @param patientDTO DTO con datos del paciente
     * @return Mono con el DTO del paciente creado
     */
    @PostMapping(value = "/api/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PatientDTO> createPatient(@RequestBody PatientCreateDTO patientDTO) {
        return patientCreateUseCase.createPatient(patientDTO);
    }
    
    /**
     * Actualiza un paciente existente
     * @param id ID del paciente a actualizar
     * @param patientDTO DTO con datos actualizados
     * @return Mono con el DTO del paciente actualizado
     */
    @PutMapping(value = "/api/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PatientDTO> updatePatient(
            @PathVariable String id, 
            @RequestBody PatientCreateDTO patientDTO) {
        return patientUpdateUseCase.updatePatient(id, patientDTO);
    }
    
    /**
     * Elimina un paciente
     * @param id ID del paciente a eliminar
     * @return Mono vacío que completa cuando se elimina el paciente
     */
    @DeleteMapping("/api/patients/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePatient(@PathVariable String id) {
        return patientDeleteUseCase.deletePatient(id);
    }
    
    /**
     * Busca pacientes por nombre o apellido
     * @param query Texto a buscar
     * @return Flux de DTOs de pacientes que coinciden con la búsqueda
     */
    @GetMapping(value = "/api/patients/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<PatientDTO> searchPatients(@RequestParam String query) {
        return patientQueryUseCase.searchPatients(query);
    }
} 