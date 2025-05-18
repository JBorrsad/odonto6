package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.request.MedicalEntryCreateDTO;
import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.port.in.medicalrecord.MedicalRecordQueryUseCase;
import odoonto.application.port.in.medicalrecord.MedicalEntryAddUseCase;
import odoonto.application.exceptions.MedicalRecordNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para operaciones con historiales médicos
 */
@RestController
public class MedicalRecordController {

    private final MedicalRecordQueryUseCase medicalRecordQueryUseCase;
    private final MedicalEntryAddUseCase medicalEntryAddUseCase;

    @Autowired
    public MedicalRecordController(
            MedicalRecordQueryUseCase medicalRecordQueryUseCase,
            MedicalEntryAddUseCase medicalEntryAddUseCase) {
        this.medicalRecordQueryUseCase = medicalRecordQueryUseCase;
        this.medicalEntryAddUseCase = medicalEntryAddUseCase;
    }

    /**
     * Obtiene un historial médico por su ID
     * @param id ID del historial médico
     * @return Mono con el DTO del historial médico
     */
    @GetMapping(value = "/api/medical-records/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MedicalRecordDTO> getMedicalRecordById(@PathVariable String id) {
        return medicalRecordQueryUseCase.findById(id)
                .onErrorResume(MedicalRecordNotFoundException.class, e -> Mono.empty());
    }
    
    /**
     * Obtiene todos los historiales médicos
     * @return Flux con los DTOs de los historiales médicos
     */
    @GetMapping(value = "/api/medical-records", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<MedicalRecordDTO> getAllMedicalRecords() {
        return medicalRecordQueryUseCase.findAll()
                .onErrorResume(e -> Flux.empty());
    }
    
    /**
     * Añade una entrada al historial médico
     * @param id ID del historial médico
     * @param entryDescription Descripción de la entrada
     * @return Mono que completa cuando la operación termina
     */
    @PostMapping(value = "/api/medical-records/{id}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addMedicalEntry(
            @PathVariable String id,
            @RequestParam String entryDescription) {
        
        MedicalEntryCreateDTO entryDTO = new MedicalEntryCreateDTO();
        entryDTO.setDescription(entryDescription);
        entryDTO.setType("nota"); // Tipo por defecto
        
        return medicalEntryAddUseCase.addEntry(id, entryDTO)
                .onErrorResume(e -> Mono.error(new MedicalRecordNotFoundException("Error al añadir entrada: " + e.getMessage())));
    }
    
    /**
     * Obtiene el historial médico de un paciente
     * @param patientId ID del paciente
     * @return Mono con el DTO del historial médico del paciente
     */
    @GetMapping(value = "/api/patients/{patientId}/medical-record", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MedicalRecordDTO> getPatientMedicalRecord(@PathVariable String patientId) {
        return medicalRecordQueryUseCase.findByPatientId(patientId)
                .onErrorResume(e -> Mono.empty());
    }
} 